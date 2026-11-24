package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.PerfilRequest;
import cl.duoc.artesanias_backend.dto.UsuarioResponse;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.model.Usuario;
import cl.duoc.artesanias_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Busca al usuario logueado a partir del email que viene en el token
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + email));
    }

    public UsuarioResponse obtenerPerfil(String email) {
        return UsuarioResponse.desde(buscarPorEmail(email));
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(String email, PerfilRequest datos) {
        Usuario usuario = buscarPorEmail(email);
        usuario.setNombre(datos.nombre());
        usuario.setTelefono(datos.telefono());
        usuario.setDireccion(datos.direccion());
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }
}
