package cl.duoc.artesanias_backend.dto;

import cl.duoc.artesanias_backend.model.Usuario;

// Datos públicos del usuario (sin la contraseña)
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        String direccion,
        String rol
) {
    // Convierte la entidad Usuario en este DTO
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getTipoUsuario().getNombre()
        );
    }
}
