package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ClasificacionRequest;
import cl.duoc.artesanias_backend.exception.RecursoDuplicadoException;
import cl.duoc.artesanias_backend.model.Clasificacion;
import cl.duoc.artesanias_backend.repository.ClasificacionRepository;
import cl.duoc.artesanias_backend.repository.EstadoPedidoRepository;
import cl.duoc.artesanias_backend.repository.EstadoProductoRepository;
import cl.duoc.artesanias_backend.repository.MetodoPagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private ClasificacionRepository clasificacionRepository;
    @Mock
    private EstadoProductoRepository estadoProductoRepository;
    @Mock
    private EstadoPedidoRepository estadoPedidoRepository;
    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    @Test
    void crearClasificacion_guardaNombreSinEspacios() {
        when(clasificacionRepository.existsByNombreIgnoreCase("Vidrio")).thenReturn(false);
        when(clasificacionRepository.save(any(Clasificacion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Clasificacion creada = catalogoService.crearClasificacion(new ClasificacionRequest("  Vidrio "));

        assertEquals("Vidrio", creada.getNombre());
    }

    @Test
    void crearClasificacion_repetida_lanzaDuplicado() {
        when(clasificacionRepository.existsByNombreIgnoreCase("Cerámica")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class,
                () -> catalogoService.crearClasificacion(new ClasificacionRequest("Cerámica")));
        verify(clasificacionRepository, never()).save(any(Clasificacion.class));
    }
}
