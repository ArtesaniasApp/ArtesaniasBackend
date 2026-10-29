package cl.duoc.artesanias_backend.dto;

import jakarta.validation.constraints.NotNull;

// Para cambiar el estado de un producto o de un pedido (envía el id del estado nuevo)
public record EstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        Long estadoId
) {
}
