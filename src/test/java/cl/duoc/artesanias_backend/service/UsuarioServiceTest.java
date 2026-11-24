package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.PerfilRequest;
import cl.duoc.artesanias_backend.dto.UsuarioResponse;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.model.TipoUsuario;
import cl.duoc.artesanias_backend.model.Usuario;
import cl.duoc.artesanias_backend.repository.UsuarioRepository;
import cl.duoc.artesanias_backend.util.Constantes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void actualizarPerfil_cambiaDatosEditables() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setEmail("cliente@test.cl");
        usuario.setNombre("Antes");
        usuario.setTipoUsuario(new TipoUsuario(2L, Constantes.ROL_CLIENTE));

        when(usuarioRepository.findByEmail("cliente@test.cl")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        UsuarioResponse respuesta = usuarioService.actualizarPerfil("cliente@test.cl",
                new PerfilRequest("Después", "+56911111111", "Nueva dirección 1"));

        assertEquals("Después", respuesta.nombre());
        assertEquals("Nueva dirección 1", respuesta.direccion());
        assertEquals("cliente@test.cl", respuesta.email());
    }

    @Test
    void buscarPorEmail_inexistente_lanzaNoEncontrado() {
        when(usuarioRepository.findByEmail("nadie@test.cl")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.buscarPorEmail("nadie@test.cl"));
    }
}
