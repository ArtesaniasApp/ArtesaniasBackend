package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.model.*;
import cl.duoc.artesanias_backend.repository.FavoritoRepository;
import cl.duoc.artesanias_backend.util.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ProductoService productoService;

    @InjectMocks
    private FavoritoService favoritoService;

    private Usuario cliente;
    private Producto producto;

    @BeforeEach
    void prepararDatos() {
        cliente = new Usuario();
        cliente.setId(10L);
        cliente.setEmail("cliente@test.cl");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Taza");
        producto.setClasificacion(new Clasificacion(1L, "Cerámica"));
        producto.setEstadoProducto(new EstadoProducto(1L, Constantes.PRODUCTO_DISPONIBLE));
    }

    @Test
    void agregar_guardaFavorito() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(productoService.buscarProductoVisible(1L)).thenReturn(producto);
        when(favoritoRepository.existsById(new FavoritoId(10L, 1L))).thenReturn(false);

        favoritoService.agregar("cliente@test.cl", 1L);

        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    void agregar_yaExistente_noDuplica() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(productoService.buscarProductoVisible(1L)).thenReturn(producto);
        when(favoritoRepository.existsById(new FavoritoId(10L, 1L))).thenReturn(true);

        favoritoService.agregar("cliente@test.cl", 1L);

        verify(favoritoRepository, never()).save(any(Favorito.class));
    }

    @Test
    void agregar_productoInactivo_lanzaNoEncontrado() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(productoService.buscarProductoVisible(1L)).thenThrow(new RecursoNoEncontradoException("Producto no encontrado: 1"));

        assertThrows(RecursoNoEncontradoException.class, () -> favoritoService.agregar("cliente@test.cl", 1L));
        verify(favoritoRepository, never()).save(any(Favorito.class));
    }

    @Test
    void eliminar_noEstaEnFavoritos_lanzaNoEncontrado() {
        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(favoritoRepository.existsById(new FavoritoId(10L, 1L))).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> favoritoService.eliminar("cliente@test.cl", 1L));
        verify(favoritoRepository, never()).deleteById(any());
    }

    @Test
    void listar_ocultaProductosInactivos() {
        Producto inactivo = new Producto();
        inactivo.setId(2L);

        when(usuarioService.buscarPorEmail("cliente@test.cl")).thenReturn(cliente);
        when(favoritoRepository.findByUsuarioIdOrderByFechaAgregadoDesc(10L)).thenReturn(List.of(
                new Favorito(new FavoritoId(10L, 1L), cliente, producto, LocalDateTime.now()),
                new Favorito(new FavoritoId(10L, 2L), cliente, inactivo, LocalDateTime.now())));
        when(productoService.estaInactivo(producto)).thenReturn(false);
        when(productoService.estaInactivo(inactivo)).thenReturn(true);

        List<ProductoResponse> favoritos = favoritoService.listar("cliente@test.cl");

        assertEquals(1, favoritos.size());
        assertEquals(1L, favoritos.get(0).id());
    }
}
