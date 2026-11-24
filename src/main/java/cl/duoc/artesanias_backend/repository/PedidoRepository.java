package cl.duoc.artesanias_backend.repository;

import cl.duoc.artesanias_backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Pedidos de un cliente, del más nuevo al más antiguo
    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // Todos los pedidos (vista admin)
    List<Pedido> findAllByOrderByFechaDesc();

    // Pedidos filtrados por estado (vista admin)
    List<Pedido> findByEstadoPedidoIdOrderByFechaDesc(Long estadoPedidoId);
}
