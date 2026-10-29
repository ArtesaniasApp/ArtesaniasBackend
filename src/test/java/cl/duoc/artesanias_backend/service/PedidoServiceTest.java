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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias: se reemplazan los repositorios por "mocks" (objetos falsos),
// así se prueba solo la lógica de PedidoService, sin base de datos.
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private EstadoPedidoRepository estadoPedidoRepository;
    @Mock
    private MetodoPagoRepository metodoPagoRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ProductoService productoService;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario cliente;
    private MetodoPago metodoPago;
    private Producto producto;

    @BeforeEach
    void prepararDatos() {
        TipoUsuario rolCliente = new TipoUsuario(2L, Constantes.ROL_CLIENTE);

        cliente = new Usuario();
        cliente.setId(10L);
        cliente.setTipoUsuario(rolCliente);
        cliente.setNombre("Cliente Test");
        cliente.setEmail("cliente@test.cl");
        cliente.setDireccion("Calle Falsa 123");

        metodoPago = new MetodoPago(1L, "TARJETA_DEBITO");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Taza de greda");
        producto.setPrecioBase(5000);
        producto.setStockDisponible(10);
        producto.setEstadoProducto(new EstadoProducto(1L, Constantes.PRODUCTO_DISPONIBLE));
        producto.setClasificacion(new Clasificacion(1L, "Cerámica"));
        producto.setImagenUrl("https://ejemplo.cl/taza.jpg");
    }

    @Test
    void crearPedido_descuentaStockYCalculaTotal() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre(Constantes.PEDIDO_PENDIENTE))
                .thenReturn(Optional.of(new EstadoPedido(1L, Constantes.PEDIDO_PENDIENTE)));
        when(productoService.buscarProducto(1L)).thenReturn(producto);
        // save() devuelve el mismo pedido que recibe
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        PedidoRequest solicitud = new PedidoRequest(1L, null, List.of(new ItemPedidoRequest(1L, 3)));

        PedidoResponse respuesta = pedidoService.crear("cliente@test.cl", solicitud);

        assertEquals(15000, respuesta.total());                       // 3 x 5000
        assertEquals(7, producto.getStockDisponible());               // 10 - 3
        assertEquals("Calle Falsa 123", respuesta.direccionDespacho()); // dirección del perfil
        assertEquals(Constantes.PEDIDO_PENDIENTE, respuesta.estado());
        assertEquals(1, respuesta.detalles().size());
    }

    @Test
    void crearPedido_sinStockSuficiente_lanzaError() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre(Constantes.PEDIDO_PENDIENTE))
                .thenReturn(Optional.of(new EstadoPedido(1L, Constantes.PEDIDO_PENDIENTE)));
        when(productoService.buscarProducto(1L)).thenReturn(producto);

        PedidoRequest solicitud = new PedidoRequest(1L, null, List.of(new ItemPedidoRequest(1L, 50)));

        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear("cliente@test.cl", solicitud));

        // Como falló, el pedido nunca se guarda
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_devuelveElStock() {
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setUsuario(cliente);
        pedido.setMetodoPago(metodoPago);
        pedido.setFecha(LocalDateTime.now());
        pedido.setDireccionDespacho("Calle Falsa 123");
        pedido.setTotal(10000);
        pedido.setEstadoPedido(new EstadoPedido(1L, Constantes.PEDIDO_PENDIENTE));

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(5000);
        pedido.getDetalles().add(detalle);

        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(6L))
                .thenReturn(Optional.of(new EstadoPedido(6L, Constantes.PEDIDO_CANCELADA)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        PedidoResponse respuesta = pedidoService.cambiarEstado(5L, 6L);

        assertEquals(Constantes.PEDIDO_CANCELADA, respuesta.estado());
        assertEquals(12, producto.getStockDisponible()); // 10 + 2 devueltos
    }

    @Test
    void pedidoFinalizado_noPuedeCambiarDeEstado() {
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setEstadoPedido(new EstadoPedido(5L, Constantes.PEDIDO_FINALIZADA));

        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(6L))
                .thenReturn(Optional.of(new EstadoPedido(6L, Constantes.PEDIDO_CANCELADA)));

        assertThrows(ReglaNegocioException.class, () -> pedidoService.cambiarEstado(5L, 6L));
    }

    @Test
    void crearPedido_usaDireccionEnviada() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre(Constantes.PEDIDO_PENDIENTE))
                .thenReturn(Optional.of(new EstadoPedido(1L, Constantes.PEDIDO_PENDIENTE)));
        when(productoService.buscarProducto(1L)).thenReturn(producto);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        PedidoRequest solicitud = new PedidoRequest(1L, "Otra calle 456", List.of(new ItemPedidoRequest(1L, 1)));

        assertEquals("Otra calle 456", pedidoService.crear("cliente@test.cl", solicitud).direccionDespacho());
    }

    @Test
    void crearPedido_productoInactivo_lanzaError() {
        producto.setEstadoProducto(new EstadoProducto(3L, Constantes.PRODUCTO_INACTIVO));
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre(Constantes.PEDIDO_PENDIENTE))
                .thenReturn(Optional.of(new EstadoPedido(1L, Constantes.PEDIDO_PENDIENTE)));
        when(productoService.buscarProducto(1L)).thenReturn(producto);

        PedidoRequest solicitud = new PedidoRequest(1L, null, List.of(new ItemPedidoRequest(1L, 1)));

        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear("cliente@test.cl", solicitud));
        assertEquals(10, producto.getStockDisponible());
    }

    @Test
    void crearPedido_metodoPagoInexistente_lanzaNoEncontrado() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(metodoPagoRepository.findById(99L)).thenReturn(Optional.empty());

        PedidoRequest solicitud = new PedidoRequest(99L, null, List.of(new ItemPedidoRequest(1L, 1)));

        assertThrows(RecursoNoEncontradoException.class, () -> pedidoService.crear("cliente@test.cl", solicitud));
    }

    @Test
    void cambiarEstado_avanzaAlSiguiente() {
        Pedido pedido = crearPedido(Constantes.PEDIDO_PENDIENTE);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(2L))
                .thenReturn(Optional.of(new EstadoPedido(2L, Constantes.PEDIDO_RECIBIDA)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        assertEquals(Constantes.PEDIDO_RECIBIDA, pedidoService.cambiarEstado(5L, 2L).estado());
    }

    @Test
    void cambiarEstado_saltarPasos_lanzaError() {
        Pedido pedido = crearPedido(Constantes.PEDIDO_PENDIENTE);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(5L))
                .thenReturn(Optional.of(new EstadoPedido(5L, Constantes.PEDIDO_FINALIZADA)));

        assertThrows(ReglaNegocioException.class, () -> pedidoService.cambiarEstado(5L, 5L));
    }

    @Test
    void cambiarEstado_retroceder_lanzaError() {
        Pedido pedido = crearPedido(Constantes.PEDIDO_DESPACHADA);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findById(1L))
                .thenReturn(Optional.of(new EstadoPedido(1L, Constantes.PEDIDO_PENDIENTE)));

        assertThrows(ReglaNegocioException.class, () -> pedidoService.cambiarEstado(5L, 1L));
    }

    @Test
    void obtener_pedidoDeOtroCliente_lanzaAccesoDenegado() {
        Usuario otro = new Usuario();
        otro.setId(20L);
        otro.setTipoUsuario(new TipoUsuario(2L, Constantes.ROL_CLIENTE));

        when(usuarioService.buscarPorEmail("otro@test.cl")).thenReturn(otro);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(crearPedido(Constantes.PEDIDO_PENDIENTE)));

        assertThrows(AccesoDenegadoException.class, () -> pedidoService.obtener("otro@test.cl", 5L));
    }

    @Test
    void obtener_adminVePedidoAjeno() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setTipoUsuario(new TipoUsuario(1L, Constantes.ROL_ADMIN));

        when(usuarioService.buscarPorEmail("admin@test.cl")).thenReturn(admin);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(crearPedido(Constantes.PEDIDO_PENDIENTE)));

        assertEquals(5L, pedidoService.obtener("admin@test.cl", 5L).id());
    }

    private Pedido crearPedido(String estado) {
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setUsuario(cliente);
        pedido.setMetodoPago(metodoPago);
        pedido.setFecha(LocalDateTime.now());
        pedido.setDireccionDespacho("Calle Falsa 123");
        pedido.setTotal(5000);
        pedido.setEstadoPedido(new EstadoPedido(1L, estado));
        return pedido;
    }
}
