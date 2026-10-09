package proyectofinal.BackEnd.Cuentas.Service;

import org.springframework.stereotype.Service;
import proyectofinal.BackEnd.Cuentas.Repository.CuentaRepository;
import proyectofinal.BackEnd.Cuentas.models.DTO.CuentaDto;
import proyectofinal.BackEnd.Cuentas.models.Entity.CuentaEntity;
import proyectofinal.BackEnd.Movimientos.Repository.MovimientosRepository;
import proyectofinal.BackEnd.Movimientos.models.Entity.MovimientosEntity;
import proyectofinal.BackEnd.Usuarios.Repository.UsuariosRepository;
import proyectofinal.BackEnd.Usuarios.models.Entity.UsuariosEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepo;
    private final UsuariosRepository usuariosRepo;
    private final MovimientosRepository movimientosRepo;

    public CuentaService(CuentaRepository cuentaRepo, UsuariosRepository usuariosRepo, MovimientosRepository movimientosRepo) {
        this.cuentaRepo = cuentaRepo;
        this.usuariosRepo = usuariosRepo;
        this.movimientosRepo = movimientosRepo;
    }

    private BigDecimal sumarMovimientos(Long idCuenta, String tipo) {
        BigDecimal total = BigDecimal.ZERO;
        List<MovimientosEntity> movimientos = movimientosRepo.findByCuentaIdCuenta(idCuenta);
        for (MovimientosEntity mov : movimientos) {
            if (mov.getTipo().equals(tipo)) {
                total = total.add(mov.getMonto());
            }
        }
        return total;
    }

    private CuentaDto convertirADto(CuentaEntity entity) {
        BigDecimal ingresos = sumarMovimientos(entity.getIdCuenta(), "INGRESO");
        BigDecimal gastos = sumarMovimientos(entity.getIdCuenta(), "GASTO");

        CuentaDto dto = new CuentaDto();
        dto.setIdCuenta(entity.getIdCuenta());
        dto.setIdUsuario(entity.getUsuario().getIdUsuario());
        dto.setNombre(entity.getNombre());
        dto.setTipo(entity.getTipo());
        dto.setSaldoInicial(entity.getSaldoInicial());
        dto.setActiva(entity.getActiva());
        dto.setTotalIngresos(ingresos);
        dto.setTotalGastos(gastos);
        dto.setSaldoActual(entity.getSaldoInicial().add(ingresos).subtract(gastos));
        return dto;
    }

    private void validarTipo(String tipo) {
        if (!tipo.equals("EFECTIVO") && !tipo.equals("BANCO") && !tipo.equals("TARJETA")) {
            throw new IllegalArgumentException("El tipo debe ser EFECTIVO, BANCO o TARJETA");
        }
    }

    //POST
    public CuentaDto crearCuenta(CuentaDto dto) {
        UsuariosEntity usuario = usuariosRepo.findById(dto.getIdUsuario()).orElse(null);
        if (usuario == null) {
            return null;
        }
        validarTipo(dto.getTipo());

        CuentaEntity entity = new CuentaEntity();
        entity.setUsuario(usuario);
        entity.setNombre(dto.getNombre());
        entity.setTipo(dto.getTipo());
        entity.setSaldoInicial(dto.getSaldoInicial());
        if (dto.getActiva() == null) {
            entity.setActiva("S");
        } else {
            entity.setActiva(dto.getActiva());
        }
        CuentaEntity nuevaCuenta = cuentaRepo.save(entity);
        return convertirADto(nuevaCuenta);
    }

    public List<CuentaDto> listarPorUsuario(Long idUsuario) {
        if (!usuariosRepo.existsById(idUsuario)) {
            return null;
        }
        List<CuentaEntity> list = cuentaRepo.findByUsuarioIdUsuario(idUsuario);
        List<CuentaDto> listDto = new ArrayList<>();
        for (CuentaEntity entity : list) {
            listDto.add(convertirADto(entity));
        }
        return listDto;
    }

    public CuentaDto obtenerCuenta(Long id) {
        CuentaEntity entity = cuentaRepo.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return convertirADto(entity);
    }

    //PUT
    public CuentaDto modificarCuenta(Long id, CuentaDto dto) {
        CuentaEntity entity = cuentaRepo.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        validarTipo(dto.getTipo());

        entity.setNombre(dto.getNombre());
        entity.setTipo(dto.getTipo());
        entity.setSaldoInicial(dto.getSaldoInicial());

        if (dto.getActiva() != null) {
            //Solo se puede desactivar si el saldo actual es 0
            if (dto.getActiva().equals("N") && entity.getActiva().equals("S")) {
                BigDecimal saldoActual = convertirADto(entity).getSaldoActual();
                if (saldoActual.compareTo(BigDecimal.ZERO) != 0) {
                    throw new IllegalArgumentException("Solo se puede desactivar una cuenta con saldo actual de $0.00");
                }
            }
            entity.setActiva(dto.getActiva());
        }
        CuentaEntity actualizada = cuentaRepo.save(entity);
        return convertirADto(actualizada);
    }

    //PUT (desactivar)
    public CuentaDto desactivarCuenta(Long id) {
        CuentaEntity entity = cuentaRepo.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        if (entity.getActiva().equals("N")) {
            throw new IllegalArgumentException("La cuenta ya se encuentra desactivada");
        }
        BigDecimal saldoActual = convertirADto(entity).getSaldoActual();
        if (saldoActual.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Solo se puede desactivar una cuenta con saldo actual de $0.00");
        }
        entity.setActiva("N");
        CuentaEntity actualizada = cuentaRepo.save(entity);
        return convertirADto(actualizada);
    }

    //DELETE
    public void eliminarCuenta(Long id) {
        List<MovimientosEntity> movimientos = movimientosRepo.findByCuentaIdCuenta(id);
        if (!movimientos.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la cuenta porque tiene movimientos asociados");
        }
        cuentaRepo.deleteById(id);
    }
}
