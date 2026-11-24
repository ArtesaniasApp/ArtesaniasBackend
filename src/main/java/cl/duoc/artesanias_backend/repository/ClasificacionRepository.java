package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.Clasificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClasificacionRepository extends JpaRepository<Clasificacion, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
