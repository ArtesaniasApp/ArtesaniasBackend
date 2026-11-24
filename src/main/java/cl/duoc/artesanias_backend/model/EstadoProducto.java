package cl.duoc.artesanias_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tabla estado_producto: DISPONIBLE, AGOTADO, INACTIVO
@Entity
@Table(name = "estado_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
