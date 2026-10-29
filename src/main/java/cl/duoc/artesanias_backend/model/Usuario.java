package cl.duoc.artesanias_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tabla usuario: clientes y administradores
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos usuarios tienen un tipo de usuario
    @ManyToOne
    @JoinColumn(name = "tipo_usuario_id", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    // Se guarda encriptada con BCrypt, nunca en texto plano
    @Column(nullable = false)
    private String password;

    // Dirección de despacho (requisito del profesor para el registro)
    private String direccion;
}
