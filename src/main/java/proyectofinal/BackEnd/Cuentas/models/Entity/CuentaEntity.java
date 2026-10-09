package proyectofinal.BackEnd.Cuentas.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import proyectofinal.BackEnd.Usuarios.models.Entity.UsuariosEntity;

import java.math.BigDecimal;

@Getter @Setter
@Entity
@Table(name = "CUENTAS")
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUENTA")
    private Long idCuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO")
    private UsuariosEntity usuario;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "SALDO_INICIAL")
    private BigDecimal saldoInicial;

    @Column(name = "ACTIVA", columnDefinition = "CHAR(1)")
    private String activa;
}
