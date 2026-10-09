package proyectofinal.BackEnd.Usuarios.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "USUARIOS")
public class UsuariosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "CORREO")
    private String correo;

    // La base de datos asigna SYSDATE por defecto
    @Column(name = "FECHA_REGISTRO", insertable = false, updatable = false)
    private LocalDate fechaRegistro;
}
