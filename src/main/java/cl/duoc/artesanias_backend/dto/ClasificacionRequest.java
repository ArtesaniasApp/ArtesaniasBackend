package cl.duoc.artesanias_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ClasificacionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
