package cl.duoc.artesanias_backend.dto;

// Respuesta de login y registro: el token JWT y los datos del usuario
public record AuthResponse(
        String token,
        UsuarioResponse usuario
) {
}
