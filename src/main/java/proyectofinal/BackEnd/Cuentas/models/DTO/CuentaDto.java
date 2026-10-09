package proyectofinal.BackEnd.Cuentas.models.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CuentaDto {
    private Long idCuenta;

    @NotNull(message = "El usuario de la cuenta es obligatorio")
    private Long idUsuario;

    @NotBlank(message = "El nombre de la cuenta es obligatorio")
    @Size(max = 100, message = "El nombre de la cuenta no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private String tipo;

    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El saldo inicial admite máximo 10 enteros y 2 decimales")
    private BigDecimal saldoInicial;

    @Pattern(regexp = "S|N", message = "El estado debe ser S (activa) o N (inactiva)")
    private String activa;

    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal saldoActual;
}
