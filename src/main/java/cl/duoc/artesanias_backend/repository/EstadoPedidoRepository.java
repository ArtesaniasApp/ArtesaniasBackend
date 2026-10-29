package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {

    Optional<EstadoPedido> findByNombre(String nombre);
}
