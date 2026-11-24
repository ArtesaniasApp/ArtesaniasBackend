package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.AuthResponse;
import cl.duoc.artesanias_backend.dto.LoginRequest;
import cl.duoc.artesanias_backend.dto.RegistroRequest;
import cl.duoc.artesanias_backend.exception.CredencialesInvalidasException;
import cl.duoc.artesanias_backend.exception.RecursoDuplicadoException;
import cl.duoc.artesanias_backend.model.TipoUsuario;
import cl.duoc.artesanias_backend.model.Usuario;
import cl.duoc.artesanias_backend.repository.TipoUsuarioRepository;
import cl.duoc.artesanias_backend.repository.UsuarioRepository;
import cl.duoc.artesanias_backend.security.JwtService;
import cl.duoc.artesanias_backend.util.Constantes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas de registro y login
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_creaClienteConContrasenaEncriptada() {
        RegistroRequest datos = new RegistroRequest("Ana", "  Ana@Correo.CL ", "+569", "secreto123", "Calle 1");

        when(usuarioRepository.existsByEmail("ana@correo.cl")).thenReturn(false);
        when(tipoUsuarioRepository.findByNombre(Constantes.ROL_CLIENTE))
                .thenReturn(Optional.of(new TipoUsuario(2L, Constantes.ROL_CLIENTE)));
        when(passwordEncoder.encode("secreto123")).thenReturn("HASH");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token-falso");

        AuthResponse respuesta = authService.registrar(datos);

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(guardado.capture());
        assertEquals("HASH", guardado.getValue().getPassword());
        assertEquals("ana@correo.cl", respuesta.usuario().email());
        assertEquals(Constantes.ROL_CLIENTE, respuesta.usuario().rol());
        assertEquals("token-falso", respuesta.token());
    }

    @Test
    void registrar_emailRepetido_lanzaDuplicado() {
        RegistroRequest datos = new RegistroRequest("Ana", "ana@correo.cl", null, "secreto123", "Calle 1");

        when(usuarioRepository.existsByEmail("ana@correo.cl")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> authService.registrar(datos));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_correcto_devuelveToken() {
        Usuario usuario = crearUsuario();

        when(usuarioRepository.findByEmail("ana@correo.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secreto123", "HASH")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("token-falso");

        AuthResponse respuesta = authService.login(new LoginRequest("ANA@correo.cl", "secreto123"));

        assertEquals("token-falso", respuesta.token());
    }

    @Test
    void login_contrasenaIncorrecta_lanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("ana@correo.cl")).thenReturn(Optional.of(crearUsuario()));
        when(passwordEncoder.matches("mala", "HASH")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new LoginRequest("ana@correo.cl", "mala")));
    }

    @Test
    void login_emailInexistente_lanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("nadie@correo.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new LoginRequest("nadie@correo.cl", "secreto123")));
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");
        usuario.setEmail("ana@correo.cl");
        usuario.setPassword("HASH");
        usuario.setTipoUsuario(new TipoUsuario(2L, Constantes.ROL_CLIENTE));
        return usuario;
    }
}
