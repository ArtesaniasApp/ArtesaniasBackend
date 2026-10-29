package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ProductoRequest;
import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.model.Clasificacion;
import cl.duoc.artesanias_backend.model.EstadoProducto;
import cl.duoc.artesanias_backend.model.Producto;
import cl.duoc.artesanias_backend.repository.ClasificacionRepository;
import cl.duoc.artesanias_backend.repository.EstadoProductoRepository;
import cl.duoc.artesanias_backend.repository.ProductoRepository;
import cl.duoc.artesanias_backend.util.Constantes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas del control de stock automático y del inventario
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ClasificacionRepository clasificacionRepository;
    @Mock
    private EstadoProductoRepository estadoProductoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void stockCero_dejaProductoAgotado() {
        Producto producto = new Producto();
        producto.setStockDisponible(0);
        producto.setEstadoProducto(new EstadoProducto(1L, Constantes.PRODUCTO_DISPONIBLE));

        when(estadoProductoRepository.findByNombre(Constantes.PRODUCTO_AGOTADO))
                .thenReturn(Optional.of(new EstadoProducto(2L, Constantes.PRODUCTO_AGOTADO)));

        productoService.actualizarEstadoSegunStock(producto);

        assertEquals(Constantes.PRODUCTO_AGOTADO, producto.getEstadoProducto().getNombre());
    }

    @Test
    void reponerStock_vuelveADisponible() {
        Producto producto = new Producto();
        producto.setStockDisponible(5);
        producto.setEstadoProducto(new EstadoProducto(2L, Constantes.PRODUCTO_AGOTADO));

        when(estadoProductoRepository.findByNombre(Constantes.PRODUCTO_DISPONIBLE))
                .thenReturn(Optional.of(new EstadoProducto(1L, Constantes.PRODUCTO_DISPONIBLE)));

        productoService.actualizarEstadoSegunStock(producto);

        assertEquals(Constantes.PRODUCTO_DISPONIBLE, producto.getEstadoProducto().getNombre());
    }

    @Test
    void productoInactivo_sigueInactivoAunqueTengaStock() {
        Producto producto = new Producto();
        producto.setStockDisponible(5);
        producto.setEstadoProducto(new EstadoProducto(3L, Constantes.PRODUCTO_INACTIVO));

        productoService.actualizarEstadoSegunStock(producto);

        assertEquals(Constantes.PRODUCTO_INACTIVO, producto.getEstadoProducto().getNombre());
    }

    @Test
    void obtenerProductoInactivo_lanzaNoEncontrado() {
        Producto producto = new Producto();
        producto.setEstadoProducto(new EstadoProducto(3L, Constantes.PRODUCTO_INACTIVO));

        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.obtener(7L));
    }

    @Test
    void cambiarEstadoADisponibleSinStock_quedaAgotado() {
        Producto producto = crearProducto(0, Constantes.PRODUCTO_INACTIVO);

        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));
        when(estadoProductoRepository.findById(1L))
                .thenReturn(Optional.of(new EstadoProducto(1L, Constantes.PRODUCTO_DISPONIBLE)));
        when(estadoProductoRepository.findByNombre(Constantes.PRODUCTO_AGOTADO))
                .thenReturn(Optional.of(new EstadoProducto(2L, Constantes.PRODUCTO_AGOTADO)));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        assertEquals(Constantes.PRODUCTO_AGOTADO, productoService.cambiarEstado(7L, 1L).estado());
    }

    @Test
    void desactivar_dejaProductoInactivo() {
        Producto producto = crearProducto(5, Constantes.PRODUCTO_DISPONIBLE);

        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));
        when(estadoProductoRepository.findByNombre(Constantes.PRODUCTO_INACTIVO))
                .thenReturn(Optional.of(new EstadoProducto(3L, Constantes.PRODUCTO_INACTIVO)));

        productoService.desactivar(7L);

        assertEquals(Constantes.PRODUCTO_INACTIVO, producto.getEstadoProducto().getNombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void crearProductoSinStock_quedaAgotado() {
        ProductoRequest datos = new ProductoRequest(1L, "Jarro", "Jarro de greda", "Greda", null, null, 9990, 0, "https://img");

        when(clasificacionRepository.findById(1L)).thenReturn(Optional.of(new Clasificacion(1L, "Cerámica")));
        when(estadoProductoRepository.findByNombre(Constantes.PRODUCTO_AGOTADO))
                .thenReturn(Optional.of(new EstadoProducto(2L, Constantes.PRODUCTO_AGOTADO)));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ProductoResponse respuesta = productoService.crear(datos);

        assertEquals(Constantes.PRODUCTO_AGOTADO, respuesta.estado());
        assertEquals("Cerámica", respuesta.clasificacion());
    }

    @Test
    void crearProductoConClasificacionInexistente_lanzaNoEncontrado() {
        ProductoRequest datos = new ProductoRequest(99L, "Jarro", "Jarro de greda", "Greda", null, null, 9990, 5, "https://img");

        when(clasificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.crear(datos));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    private Producto crearProducto(int stock, String estado) {
        Producto producto = new Producto();
        producto.setId(7L);
        producto.setNombre("Jarro");
        producto.setPrecioBase(9990);
        producto.setStockDisponible(stock);
        producto.setClasificacion(new Clasificacion(1L, "Cerámica"));
        producto.setEstadoProducto(new EstadoProducto(1L, estado));
        return producto;
    }
}
