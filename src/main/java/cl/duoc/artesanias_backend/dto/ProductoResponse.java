package cl.duoc.artesanias_backend.dto;

import cl.duoc.artesanias_backend.model.Producto;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        String material,
        String descripcionMaterial,
        String descripcionTecnica,
        Integer precioBase,
        Integer stockDisponible,
        String imagenUrl,
        Long clasificacionId,
        String clasificacion,
        String estado
) {
    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getMaterial(),
                producto.getDescripcionMaterial(),
                producto.getDescripcionTecnica(),
                producto.getPrecioBase(),
                producto.getStockDisponible(),
                producto.getImagenUrl(),
                producto.getClasificacion().getId(),
                producto.getClasificacion().getNombre(),
                producto.getEstadoProducto().getNombre()
        );
    }
}
