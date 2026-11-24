package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ItemPedidoRequest;
import cl.duoc.artesanias_backend.dto.PedidoRequest;
import cl.duoc.artesanias_backend.dto.PedidoResponse;
import cl.duoc.artesanias_backend.exception.AccesoDenegadoException;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.exception.ReglaNegocioException;
import cl.duoc.artesanias_backend.model.*;
import cl.duoc.artesanias_backend.repository.EstadoPedidoRepository;
import cl.duoc.artesanias_backend.repository.MetodoPagoRepository;
import cl.duoc.artesanias_backend.repository.PedidoRepository;
import cl.duoc.artesanias_backend.util.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final List<String> FLUJO_PEDIDO = List.of(
            Constantes.PEDIDO_PENDIENTE,
            Constantes.PEDIDO_RECIBIDA,
            Constantes.PEDIDO_EN_PREPARACION,
            Constantes.PEDIDO_DESPACHADA,
            Constantes.PEDIDO_FINALIZADA);

    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    // ---------- Cliente ----------

    // Checkout: crea el pedido, valida y descuenta stock, calcula el total y simula el pago.
    // @Transactional: si algo falla (ej: un producto sin stock) se deshace TODO, no queda un pedido a medias.
    @Transactional
    public PedidoResponse crear(String email, PedidoRequest datos) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        MetodoPago metodoPago = metodoPagoRepository.findById(datos.metodoPagoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado: " + datos.metodoPagoId()));

        // Si la app no envía dirección, se usa la del perfil
        String direccion = datos.direccionDespacho();
        if (direccion == null || direccion.isBlank()) {
            direccion = usuario.getDireccion();
        }
        if (direccion == null || direccion.isBlank()) {
            throw new ReglaNegocioException("Debe indicar una dirección de despacho");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setMetodoPago(metodoPago);
        pedido.setDireccionDespacho(direccion);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstadoPedido(buscarEstado(Constantes.PEDIDO_PENDIENTE));

        int total = 0;

        for (ItemPedidoRequest item : datos.items()) {
            Producto producto = productoService.buscarProducto(item.productoId());

            if (!producto.getEstadoProducto().getNombre().equals(Constantes.PRODUCTO_DISPONIBLE)) {
                throw new ReglaNegocioException("El producto " + producto.getNombre() + " no está disponible");
            }
            if (producto.getStockDisponible() < item.cantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre()
                        + ". Disponible: " + producto.getStockDisponible());
            }

            // Control de stock: se descuenta lo comprado
            producto.setStockDisponible(producto.getStockDisponible() - item.cantidad());
            productoService.actualizarEstadoSegunStock(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            // El precio se toma de la base de datos, no de la app (así nadie puede cambiarlo)
            detalle.setPrecioUnitario(producto.getPrecioBase());
            pedido.getDetalles().add(detalle);

            total = total + producto.getPrecioBase() * item.cantidad();
        }

        pedido.setTotal(total);

        // Pago simulado: siempre se aprueba. Aquí se conectaría una pasarela real (Webpay, etc.)
        simularPago(metodoPago, total);

        // Gracias a cascade = ALL, guardar el pedido también guarda sus detalles
        return PedidoResponse.desde(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> misPedidos(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId())
                .stream()
                .map(PedidoResponse::desde)
                .toList();
    }

    // Un cliente solo puede ver sus propios pedidos; el admin puede ver todos
    @Transactional(readOnly = true)
    public PedidoResponse obtener(String email, Long pedidoId) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Pedido pedido = buscarPedido(pedidoId);

        boolean esAdmin = usuario.getTipoUsuario().getNombre().equals(Constantes.ROL_ADMIN);
        boolean esDueno = pedido.getUsuario().getId().equals(usuario.getId());

        if (!esAdmin && !esDueno) {
            throw new AccesoDenegadoException("No tiene permiso para ver este pedido");
        }
        return PedidoResponse.desde(pedido);
    }

    // ---------- Admin ----------

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos(Long estadoId) {
        List<Pedido> pedidos;
        if (estadoId == null) {
            pedidos = pedidoRepository.findAllByOrderByFechaDesc();
        } else {
            pedidos = pedidoRepository.findByEstadoPedidoIdOrderByFechaDesc(estadoId);
        }
        return pedidos.stream().map(PedidoResponse::desde).toList();
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long pedidoId, Long estadoId) {
        Pedido pedido = buscarPedido(pedidoId);
        EstadoPedido nuevoEstado = estadoPedidoRepository.findById(estadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado de pedido no encontrado: " + estadoId));

        String estadoActual = pedido.getEstadoPedido().getNombre();
        if (estadoActual.equals(Constantes.PEDIDO_FINALIZADA) || estadoActual.equals(Constantes.PEDIDO_CANCELADA)) {
            throw new ReglaNegocioException("Un pedido " + estadoActual + " ya no puede cambiar de estado");
        }

        // Solo se avanza al estado siguiente del flujo, o se cancela
        boolean esCancelacion = nuevoEstado.getNombre().equals(Constantes.PEDIDO_CANCELADA);
        boolean esSiguiente = FLUJO_PEDIDO.indexOf(nuevoEstado.getNombre()) == FLUJO_PEDIDO.indexOf(estadoActual) + 1;
        if (!esCancelacion && !esSiguiente) {
            throw new ReglaNegocioException("Un pedido " + estadoActual + " no puede pasar a " + nuevoEstado.getNombre());
        }

        // Si se cancela, se devuelve el stock de cada producto
        if (nuevoEstado.getNombre().equals(Constantes.PEDIDO_CANCELADA)) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStockDisponible(producto.getStockDisponible() + detalle.getCantidad());
                productoService.actualizarEstadoSegunStock(producto);
            }
        }

        pedido.setEstadoPedido(nuevoEstado);
        return PedidoResponse.desde(pedidoRepository.save(pedido));
    }

    // ---------- Métodos de apoyo ----------

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
    }

    private EstadoPedido buscarEstado(String nombre) {
        return estadoPedidoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el estado de pedido " + nombre));
    }

    private void simularPago(MetodoPago metodoPago, int total) {
        if (total <= 0) {
            throw new ReglaNegocioException("El total del pedido debe ser mayor a 0");
        }
        // Pago aprobado (simulado)
    }
}
