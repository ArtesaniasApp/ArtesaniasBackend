package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoProductoRepository extends JpaRepository<EstadoProducto, Long> {

    Optional<EstadoProducto> findByNombre(String nombre);
}
