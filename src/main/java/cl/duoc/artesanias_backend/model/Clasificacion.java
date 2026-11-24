package cl.duoc.artesanias_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tabla clasificacion: categorías de artesanías (Cerámica, Textil, etc.)
@Entity
@Table(name = "clasificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
