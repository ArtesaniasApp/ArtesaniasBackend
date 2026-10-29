package cl.duoc.artesanias_backend.dto;

import jakarta.validation.constraints.NotBlank;

// Datos que el cliente puede editar de su perfil
public record PerfilRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String telefono,

        @NotBlank(message = "La dirección de despacho es obligatoria")
        String direccion
) {
}
