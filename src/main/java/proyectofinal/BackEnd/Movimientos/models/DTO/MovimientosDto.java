package proyectofinal.BackEnd.Movimientos.models.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class MovimientosDto {
    private Long idMovimiento;

    @NotNull(message = "La cuenta del movimiento es obligatoria")
    private Long idCuenta;

    @PastOrPresent(message = "La fecha del movimiento no puede ser futura")
    private LocalDate fecha;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipo;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 80, message = "La categoría no puede exceder los 80 caracteres")
    private String categoria;

    @Size(max = 300, message = "La descripción no puede exceder los 300 caracteres")
    private String descripcion;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto admite máximo 10 enteros y 2 decimales")
    private BigDecimal monto;
}
