package cl.duoc.artesanias_backend.dto;

import cl.duoc.artesanias_backend.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        LocalDateTime fecha,
        String estado,
        String metodoPago,
        String direccionDespacho,
        Integer total,
        String clienteNombre,
        String clienteEmail,
        List<DetallePedidoResponse> detalles
) {
    public static PedidoResponse desde(Pedido pedido) {
        List<DetallePedidoResponse> detalles = pedido.getDetalles()
                .stream()
                .map(DetallePedidoResponse::desde)
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getEstadoPedido().getNombre(),
                pedido.getMetodoPago().getNombre(),
                pedido.getDireccionDespacho(),
                pedido.getTotal(),
                pedido.getUsuario().getNombre(),
                pedido.getUsuario().getEmail(),
                detalles
        );
    }
}
