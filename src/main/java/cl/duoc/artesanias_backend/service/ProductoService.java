package cl.duoc.artesanias_backend.service;

import cl.duoc.artesanias_backend.dto.ProductoRequest;
import cl.duoc.artesanias_backend.dto.ProductoResponse;
import cl.duoc.artesanias_backend.exception.RecursoNoEncontradoException;
import cl.duoc.artesanias_backend.model.Clasificacion;
import cl.duoc.artesanias_backend.model.EstadoProducto;
import cl.duoc.artesanias_backend.model.Producto;
import cl.duoc.artesanias_backend.repository.ClasificacionRepository;
import cl.duoc.artesanias_backend.repository.EstadoProductoRepository;
import cl.duoc.artesanias_backend.repository.ProductoRepository;
import cl.duoc.artesanias_backend.util.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ClasificacionRepository clasificacionRepository;
    private final EstadoProductoRepository estadoProductoRepository;

    // ---------- Catálogo (cliente) ----------

    // Lista los productos visibles (todo menos INACTIVO).
    // Filtros opcionales: clasificacionId (categoría) y material. Si vienen null, no se filtra por ese campo
    public List<ProductoResponse> listarCatalogo(Long clasificacionId, String material) {
        List<Producto> productos;
        if (clasificacionId == null) {
            productos = productoRepository.findByEstadoProductoNombreNotOrderByNombreAsc(Constantes.PRODUCTO_INACTIVO);
        } else {
            productos = productoRepository.findByClasificacionIdAndEstadoProductoNombreNotOrderByNombreAsc(
                    clasificacionId, Constantes.PRODUCTO_INACTIVO);
        }
        return productos.stream()
                .filter(producto -> material == null || material.isBlank()
                        || producto.getMaterial().equalsIgnoreCase(material.trim()))
                .map(ProductoResponse::desde)
                .toList();
    }

    // Materiales distintos de los productos visibles, ordenados alfabéticamente (para el filtro de la app)
    public List<String> listarMateriales() {
        return productoRepository.findByEstadoProductoNombreNotOrderByNombreAsc(Constantes.PRODUCTO_INACTIVO)
                .stream()
                .map(Producto::getMaterial)
                .distinct()
                .sorted()
                .toList();
    }

    public ProductoResponse obtener(Long id) {
        return ProductoResponse.desde(buscarProductoVisible(id));
    }

    // ---------- Inventario (admin) ----------

    public List<ProductoResponse> listarInventario() {
        return productoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest datos) {
        Producto producto = new Producto();
        copiarDatos(datos, producto);
        actualizarEstadoSegunStock(producto);
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest datos) {
        Producto producto = buscarProducto(id);
        copiarDatos(datos, producto);
        actualizarEstadoSegunStock(producto);
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizarStock(Long id, Integer stock) {
        Producto producto = buscarProducto(id);
        producto.setStockDisponible(stock);
        actualizarEstadoSegunStock(producto);
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse cambiarEstado(Long id, Long estadoId) {
        Producto producto = buscarProducto(id);
        EstadoProducto estado = estadoProductoRepository.findById(estadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado de producto no encontrado: " + estadoId));
        producto.setEstadoProducto(estado);
        // Si no queda INACTIVO, el stock decide entre DISPONIBLE y AGOTADO
        actualizarEstadoSegunStock(producto);
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    // "Eliminar" un producto lo deja INACTIVO, porque puede estar en pedidos antiguos
    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarProducto(id);
        producto.setEstadoProducto(buscarEstado(Constantes.PRODUCTO_INACTIVO));
        productoRepository.save(producto);
    }

    // ---------- Métodos de apoyo ----------

    public Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    // Un producto INACTIVO no existe para el cliente
    public Producto buscarProductoVisible(Long id) {
        Producto producto = buscarProducto(id);
        if (estaInactivo(producto)) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + id);
        }
        return producto;
    }

    public boolean estaInactivo(Producto producto) {
        return producto.getEstadoProducto().getNombre().equals(Constantes.PRODUCTO_INACTIVO);
    }

    // Control de stock: stock 0 -> AGOTADO; si vuelve a tener stock -> DISPONIBLE.
    // Un producto INACTIVO se mantiene inactivo aunque cambie su stock.
    public void actualizarEstadoSegunStock(Producto producto) {
        EstadoProducto estadoActual = producto.getEstadoProducto();

        if (estadoActual != null && estadoActual.getNombre().equals(Constantes.PRODUCTO_INACTIVO)) {
            return;
        }

        if (producto.getStockDisponible() == 0) {
            producto.setEstadoProducto(buscarEstado(Constantes.PRODUCTO_AGOTADO));
        } else {
            producto.setEstadoProducto(buscarEstado(Constantes.PRODUCTO_DISPONIBLE));
        }
    }

    private EstadoProducto buscarEstado(String nombre) {
        return estadoProductoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el estado de producto " + nombre));
    }

    private void copiarDatos(ProductoRequest datos, Producto producto) {
        Clasificacion clasificacion = clasificacionRepository.findById(datos.clasificacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Clasificación no encontrada: " + datos.clasificacionId()));

        producto.setClasificacion(clasificacion);
        producto.setNombre(datos.nombre());
        producto.setDescripcion(datos.descripcion());
        producto.setMaterial(datos.material().trim());
        producto.setDescripcionMaterial(datos.descripcionMaterial());
        producto.setDescripcionTecnica(datos.descripcionTecnica());
        producto.setPrecioBase(datos.precioBase());
        producto.setStockDisponible(datos.stockDisponible());
        producto.setImagenUrl(datos.imagenUrl());
    }
}
