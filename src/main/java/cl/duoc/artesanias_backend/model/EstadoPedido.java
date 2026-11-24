package cl.duoc.artesanias_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tabla estado_pedido: PENDIENTE, RECIBIDA, EN_PREPARACION, DESPACHADA, FINALIZADA, CANCELADA
@Entity
@Table(name = "estado_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
