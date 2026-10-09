package proyectofinal.BackEnd.Usuarios.models.DTO;

import lombok.Getter;
import lombok.Setter;
import proyectofinal.BackEnd.Cuentas.models.DTO.CuentaDto;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class UsuariosDto {
    private Long idUsuario;
    private String nombre;
    private String correo;
    private LocalDate fechaRegistro;
    // Solo se llena al consultar un usuario específico
    private List<CuentaDto> cuentas;
}
