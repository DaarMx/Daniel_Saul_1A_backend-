package proyectofinal.BackEnd.Movimientos.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import proyectofinal.BackEnd.Cuentas.models.Entity.CuentaEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "MOVIMIENTOS")
public class MovimientosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MOVIMIENTO")
    private Long idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CUENTA", referencedColumnName = "ID_CUENTA")
    private CuentaEntity cuenta;

    @Column(name = "FECHA")
    private LocalDate fecha;

    // INGRESO o GASTO
    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "CATEGORIA")
    private String categoria;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "MONTO")
    private BigDecimal monto;
}
