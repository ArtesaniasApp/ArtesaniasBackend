package cl.duoc.artesanias_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Tabla favorito: relación muchos a muchos entre usuario y producto
@Entity
@Table(name = "favorito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {

    @EmbeddedId
    private FavoritoId id;

    // @MapsId: la columna usuario_id es a la vez parte de la clave y la relación con usuario
    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private LocalDateTime fechaAgregado;
}
