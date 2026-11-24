package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    Optional<TipoUsuario> findByNombre(String nombre);
}
