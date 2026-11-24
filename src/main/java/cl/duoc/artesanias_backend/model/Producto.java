package cl.duoc.artesanias_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tabla producto: cada artesanía del catálogo
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clasificacion_id", nullable = false)
    private Clasificacion clasificacion;

    @ManyToOne
    @JoinColumn(name = "estado_producto_id", nullable = false)
    private EstadoProducto estadoProducto;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    // Material principal en una o dos palabras (Greda, Lana, Mimbre...). Se usa para filtrar el catálogo
    @Column(nullable = false, length = 50)
    private String material;

    // Detalle del material (texto largo que se muestra en el detalle del producto)
    @Column(length = 500)
    private String descripcionMaterial;

    @Column(length = 500)
    private String descripcionTecnica;

    // Precio en pesos chilenos (CLP no usa decimales, por eso Integer)
    @Column(nullable = false)
    private Integer precioBase;

    @Column(nullable = false)
    private Integer stockDisponible;

    @Column(nullable = false)
    private String imagenUrl;
}
