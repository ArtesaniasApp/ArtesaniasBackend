package cl.duoc.artesanias_backend.config;

import cl.duoc.artesanias_backend.model.*;
import cl.duoc.artesanias_backend.repository.*;
import cl.duoc.artesanias_backend.util.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Se ejecuta una vez al iniciar el servidor y carga datos ficticios SOLO si las tablas están vacías.
// Usuarios de prueba:
//   admin@aleyleo.cl   / admin123   (ADMIN)
//   cliente@aleyleo.cl / cliente123 (CLIENTE)
@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClasificacionRepository clasificacionRepository;
    private final EstadoProductoRepository estadoProductoRepository;
    private final ProductoRepository productoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        cargarTiposUsuario();
        cargarEstadosProducto();
        cargarEstadosPedido();
        cargarMetodosPago();
        cargarUsuarios();
        cargarClasificacionesYProductos();
    }

    private void cargarTiposUsuario() {
        if (tipoUsuarioRepository.count() > 0) {
            return;
        }
        tipoUsuarioRepository.save(new TipoUsuario(null, Constantes.ROL_ADMIN));
        tipoUsuarioRepository.save(new TipoUsuario(null, Constantes.ROL_CLIENTE));
    }

    private void cargarEstadosProducto() {
        if (estadoProductoRepository.count() > 0) {
            return;
        }
        estadoProductoRepository.save(new EstadoProducto(null, Constantes.PRODUCTO_DISPONIBLE));
        estadoProductoRepository.save(new EstadoProducto(null, Constantes.PRODUCTO_AGOTADO));
        estadoProductoRepository.save(new EstadoProducto(null, Constantes.PRODUCTO_INACTIVO));
    }

    private void cargarEstadosPedido() {
        // Si la base se creó con el nombre antiguo "GENERADA", se renombra a PENDIENTE (no se pierden pedidos)
        estadoPedidoRepository.findByNombre("GENERADA").ifPresent(estado -> {
            estado.setNombre(Constantes.PEDIDO_PENDIENTE);
            estadoPedidoRepository.save(estado);
        });

        if (estadoPedidoRepository.count() > 0) {
            return;
        }
        estadoPedidoRepository.save(new EstadoPedido(null, Constantes.PEDIDO_PENDIENTE));
        estadoPedidoRepository.save(new EstadoPedido(null, Constantes.PEDIDO_RECIBIDA));
        estadoPedidoRepository.save(new EstadoPedido(null, Constantes.PEDIDO_EN_PREPARACION));
        estadoPedidoRepository.save(new EstadoPedido(null, Constantes.PEDIDO_DESPACHADA));
        estadoPedidoRepository.save(new EstadoPedido(null, Constantes.PEDIDO_FINALIZADA));
        estadoPedidoRepository.save(new EstadoPedido(null, Constantes.PEDIDO_CANCELADA));
    }

    private void cargarMetodosPago() {
        if (metodoPagoRepository.count() > 0) {
            return;
        }
        metodoPagoRepository.save(new MetodoPago(null, "TARJETA_DEBITO"));
        metodoPagoRepository.save(new MetodoPago(null, "TARJETA_CREDITO"));
        metodoPagoRepository.save(new MetodoPago(null, "TRANSFERENCIA"));
    }

    private void cargarUsuarios() {
        if (usuarioRepository.count() > 0) {
            return;
        }
        TipoUsuario admin = tipoUsuarioRepository.findByNombre(Constantes.ROL_ADMIN).orElseThrow();
        TipoUsuario cliente = tipoUsuarioRepository.findByNombre(Constantes.ROL_CLIENTE).orElseThrow();

        crearUsuario(admin, "Alejandra (Ale y Leo)", "admin@aleyleo.cl", "+56911111111",
                "admin123", "Av. Providencia 1234, Providencia, Santiago");
        crearUsuario(cliente, "Cliente de Prueba", "cliente@aleyleo.cl", "+56922222222",
                "cliente123", "Los Aromos 456, Ñuñoa, Santiago");
    }

    private void crearUsuario(TipoUsuario tipo, String nombre, String email, String telefono,
                              String password, String direccion) {
        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipo);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setDireccion(direccion);
        usuarioRepository.save(usuario);
    }

    private void cargarClasificacionesYProductos() {
        if (productoRepository.count() > 0) {
            return;
        }
        Clasificacion ceramica = clasificacionRepository.save(new Clasificacion(null, "Cerámica"));
        Clasificacion textil = clasificacionRepository.save(new Clasificacion(null, "Textil"));
        Clasificacion madera = clasificacionRepository.save(new Clasificacion(null, "Madera"));
        Clasificacion joyeria = clasificacionRepository.save(new Clasificacion(null, "Joyería"));
        Clasificacion cesteria = clasificacionRepository.save(new Clasificacion(null, "Cestería"));

        EstadoProducto disponible = estadoProductoRepository.findByNombre(Constantes.PRODUCTO_DISPONIBLE).orElseThrow();
        EstadoProducto agotado = estadoProductoRepository.findByNombre(Constantes.PRODUCTO_AGOTADO).orElseThrow();

        crearProducto(ceramica, disponible, "Taza de greda de Pomaire", "Greda",
                "Taza artesanal moldeada a mano, ideal para café o té.",
                "Greda natural de Pomaire", "Cocción a 900 °C, 300 ml", 8990, 15);
        crearProducto(ceramica, disponible, "Fuente de cerámica esmaltada", "Arcilla",
                "Fuente para servir con esmalte azul y bordes pintados a mano.",
                "Arcilla blanca esmaltada", "32 cm de diámetro, apta para horno", 24990, 6);
        crearProducto(ceramica, disponible, "Chanchito de la suerte", "Greda",
                "Clásico chanchito de greda de tres patas, símbolo de buena suerte.",
                "Greda negra", "Altura 8 cm", 4990, 30);

        crearProducto(textil, disponible, "Manta de telar mapuche", "Lana",
                "Manta tejida en telar tradicional con diseño geométrico.",
                "Lana de oveja teñida con tintes naturales", "1,2 x 1,6 m", 59990, 4);
        crearProducto(textil, disponible, "Cojín de lana tejido", "Lana",
                "Funda de cojín tejida a mano en tonos tierra.",
                "Lana merino", "45 x 45 cm, incluye relleno", 18990, 10);
        crearProducto(textil, agotado, "Bufanda de alpaca", "Alpaca",
                "Bufanda suave y abrigadora tejida a palillo.",
                "Fibra de alpaca", "180 x 30 cm", 22990, 0);

        crearProducto(madera, disponible, "Tabla de raulí para picoteo", "Madera",
                "Tabla rectangular tallada en una sola pieza.",
                "Madera de raulí con aceite natural", "40 x 20 cm", 16990, 12);
        crearProducto(madera, disponible, "Set de cucharas de olivo", "Madera",
                "Tres cucharas talladas a mano para cocina.",
                "Madera de olivo", "Largo 25 cm cada una", 12990, 20);

        crearProducto(joyeria, disponible, "Aros de lapislázuli", "Plata",
                "Aros colgantes con piedra lapislázuli chilena.",
                "Plata 950 y lapislázuli", "Largo 3 cm", 27990, 8);
        crearProducto(joyeria, disponible, "Collar de cobre martillado", "Cobre",
                "Collar con colgante de cobre trabajado a mano.",
                "Cobre chileno", "Cadena de 45 cm", 19990, 9);

        crearProducto(cesteria, disponible, "Panera de mimbre", "Mimbre",
                "Panera ovalada tejida con mimbre de Chimbarongo.",
                "Mimbre natural", "30 x 20 x 10 cm", 9990, 14);
        crearProducto(cesteria, disponible, "Canasto de coirón", "Fibra vegetal",
                "Canasto con tapa tejido en fibra vegetal.",
                "Coirón y quilineja", "Diámetro 25 cm", 21990, 5);
    }

    private void crearProducto(Clasificacion clasificacion, EstadoProducto estado, String nombre, String material,
                               String descripcion, String descripcionMaterial, String tecnica,
                               int precio, int stock) {
        Producto producto = new Producto();
        producto.setClasificacion(clasificacion);
        producto.setEstadoProducto(estado);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setMaterial(material);
        producto.setDescripcionMaterial(descripcionMaterial);
        producto.setDescripcionTecnica(tecnica);
        producto.setPrecioBase(precio);
        producto.setStockDisponible(stock);
        // Imagen de ejemplo (se puede reemplazar por fotos reales)
        producto.setImagenUrl("https://picsum.photos/seed/" + nombre.hashCode() + "/600/600");
        productoRepository.save(producto);
    }
}
