package cl.duoc.artesanias_backend.dto;

import cl.duoc.artesanias_backend.model.DetallePedido;

public record DetallePedidoResponse(
        Long productoId,
        String productoNombre,
        String imagenUrl,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal
) {
    public static DetallePedidoResponse desde(DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getProducto().getImagenUrl(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getCantidad() * detalle.getPrecioUnitario()
        );
    }
}
