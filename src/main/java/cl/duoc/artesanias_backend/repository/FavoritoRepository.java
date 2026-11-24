package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.Favorito;
import cl.duoc.artesanias_backend.model.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// El tipo de la clave es FavoritoId porque la clave es compuesta
public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    List<Favorito> findByUsuarioIdOrderByFechaAgregadoDesc(Long usuarioId);
}
