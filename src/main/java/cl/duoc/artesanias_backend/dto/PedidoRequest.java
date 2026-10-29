package cl.duoc.artesanias_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// Lo que envía la app al confirmar el carro (checkout)
public record PedidoRequest(
        @NotNull(message = "El método de pago es obligatorio")
        Long metodoPagoId,

        // Opcional: si viene vacía se usa la dirección del perfil del cliente
        String direccionDespacho,

        // @Valid hace que también se validen cada uno de los items
        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid
        List<ItemPedidoRequest> items
) {
}
