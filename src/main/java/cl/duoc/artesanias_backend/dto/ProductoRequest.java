package cl.duoc.artesanias_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// Datos para crear o editar un producto (inventario, solo admin)
public record ProductoRequest(
        @NotNull(message = "La clasificación es obligatoria")
        Long clasificacionId,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "El material es obligatorio")
        @Size(max = 50, message = "El material debe tener máximo 50 caracteres")
        String material,

        String descripcionMaterial,

        String descripcionTecnica,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a 0")
        Integer precioBase,

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stockDisponible,

        @NotBlank(message = "La URL de la imagen es obligatoria")
        String imagenUrl
) {
}
