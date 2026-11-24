package cl.duoc.artesanias_backend.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// Clave primaria compuesta de favorito: (usuario_id, producto_id)
// @Data genera equals() y hashCode(), que JPA necesita para comparar claves compuestas
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoId implements Serializable {

    private Long usuarioId;
    private Long productoId;
}
