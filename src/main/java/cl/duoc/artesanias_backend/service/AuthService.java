package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.AuthResponse;
import cl.duoc.artesanias_backend.dto.LoginRequest;
import cl.duoc.artesanias_backend.dto.RegistroRequest;
import cl.duoc.artesanias_backend.dto.UsuarioResponse;
import cl.duoc.artesanias_backend.exception.CredencialesInvalidasException;
import cl.duoc.artesanias_backend.exception.RecursoDuplicadoException;
import cl.duoc.artesanias_backend.model.TipoUsuario;
import cl.duoc.artesanias_backend.model.Usuario;
import cl.duoc.artesanias_backend.repository.TipoUsuarioRepository;
import cl.duoc.artesanias_backend.repository.UsuarioRepository;
import cl.duoc.artesanias_backend.security.JwtService;
import cl.duoc.artesanias_backend.util.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Registro: siempre crea usuarios con rol CLIENTE
    @Transactional
    public AuthResponse registrar(RegistroRequest datos) {
        String email = datos.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con el email " + email);
        }

        TipoUsuario rolCliente = tipoUsuarioRepository.findByNombre(Constantes.ROL_CLIENTE)
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE en la base de datos"));

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(rolCliente);
        usuario.setNombre(datos.nombre());
        usuario.setEmail(email);
        usuario.setTelefono(datos.telefono());
        usuario.setDireccion(datos.direccion());
        // La contraseña se guarda encriptada
        usuario.setPassword(passwordEncoder.encode(datos.password()));

        Usuario guardado = usuarioRepository.save(usuario);

        return new AuthResponse(jwtService.generarToken(guardado), UsuarioResponse.desde(guardado));
    }

    public AuthResponse login(LoginRequest datos) {
        String email = datos.email().trim().toLowerCase();

        // Mismo mensaje en ambos casos para no revelar si el email existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        // matches() compara la contraseña escrita con la encriptada
        if (!passwordEncoder.matches(datos.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        return new AuthResponse(jwtService.generarToken(usuario), UsuarioResponse.desde(usuario));
    }
}
