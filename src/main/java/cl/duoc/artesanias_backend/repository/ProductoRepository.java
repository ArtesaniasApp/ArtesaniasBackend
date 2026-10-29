package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Inventario completo (vista admin), incluye inactivos
    List<Producto> findAllByOrderByNombreAsc();

    // Catálogo para el cliente: todos los productos cuyo estado NO sea el indicado (INACTIVO)
    List<Producto> findByEstadoProductoNombreNotOrderByNombreAsc(String estado);

    // Catálogo filtrado por categoría
    List<Producto> findByClasificacionIdAndEstadoProductoNombreNotOrderByNombreAsc(Long clasificacionId, String estado);
}
