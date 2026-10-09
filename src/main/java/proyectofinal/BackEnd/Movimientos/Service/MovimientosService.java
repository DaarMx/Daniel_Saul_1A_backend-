package proyectofinal.BackEnd.Movimientos.Service;

import org.springframework.stereotype.Service;
import proyectofinal.BackEnd.Cuentas.Repository.CuentaRepository;
import proyectofinal.BackEnd.Cuentas.models.Entity.CuentaEntity;
import proyectofinal.BackEnd.Movimientos.Repository.MovimientosRepository;
import proyectofinal.BackEnd.Movimientos.models.DTO.MovimientosDto;
import proyectofinal.BackEnd.Movimientos.models.Entity.MovimientosEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovimientosService {

    private final MovimientosRepository movimientosRepo;
    private final CuentaRepository cuentaRepo;

    public MovimientosService(MovimientosRepository movimientosRepo, CuentaRepository cuentaRepo) {
        this.movimientosRepo = movimientosRepo;
        this.cuentaRepo = cuentaRepo;
    }

    private MovimientosDto convertirADto(MovimientosEntity entity) {
        MovimientosDto dto = new MovimientosDto();
        dto.setIdMovimiento(entity.getIdMovimiento());
        dto.setIdCuenta(entity.getCuenta().getIdCuenta());
        dto.setFecha(entity.getFecha());
        dto.setTipo(entity.getTipo());
        dto.setCategoria(entity.getCategoria());
        dto.setDescripcion(entity.getDescripcion());
        dto.setMonto(entity.getMonto());
        return dto;
    }

    //Los movimientos solo se pueden registrar o cambiar en cuentas activas
    private CuentaEntity buscarCuentaActiva(Long idCuenta) {
        CuentaEntity cuenta = cuentaRepo.findById(idCuenta).orElse(null);
        if (cuenta == null) {
            throw new IllegalArgumentException("No existe la cuenta con ID " + idCuenta);
        }
        if (cuenta.getActiva().equals("N")) {
            throw new IllegalArgumentException("La cuenta esta inactiva, no se pueden registrar ni cambiar sus movimientos");
        }
        return cuenta;
    }

    private void llenarEntity(MovimientosEntity entity, MovimientosDto dto, CuentaEntity cuenta) {
        if (!dto.getTipo().equals("INGRESO") && !dto.getTipo().equals("GASTO")) {
            throw new IllegalArgumentException("El tipo debe ser INGRESO o GASTO");
        }
        entity.setCuenta(cuenta);
        //Si no se envia la fecha se usa la fecha actual
        if (dto.getFecha() == null) {
            entity.setFecha(LocalDate.now());
        } else {
            entity.setFecha(dto.getFecha());
        }
        entity.setTipo(dto.getTipo());
        entity.setCategoria(dto.getCategoria());
        entity.setDescripcion(dto.getDescripcion());
        entity.setMonto(dto.getMonto());
    }

    //POST
    public MovimientosDto crearMovimiento(MovimientosDto dto) {
        CuentaEntity cuenta = buscarCuentaActiva(dto.getIdCuenta());
        MovimientosEntity entity = new MovimientosEntity();
        llenarEntity(entity, dto, cuenta);
        MovimientosEntity nuevoMovimiento = movimientosRepo.save(entity);
        return convertirADto(nuevoMovimiento);
    }

    //GET (movimientos de una cuenta, los filtros son opcionales)
    public List<MovimientosDto> listarPorCuenta(Long idCuenta, String tipo, String categoria, LocalDate desde, LocalDate hasta) {
        if (!cuentaRepo.existsById(idCuenta)) {
            return null;
        }
        List<MovimientosEntity> list = movimientosRepo.findByCuentaIdCuenta(idCuenta);
        List<MovimientosDto> listDto = new ArrayList<>();
        for (MovimientosEntity entity : list) {
            if (tipo != null && !tipo.isEmpty() && !entity.getTipo().equals(tipo)) {
                continue;
            }
            if (categoria != null && !categoria.isEmpty() && !entity.getCategoria().toLowerCase().contains(categoria.toLowerCase())) {
                continue;
            }
            if (desde != null && entity.getFecha().isBefore(desde)) {
                continue;
            }
            if (hasta != null && entity.getFecha().isAfter(hasta)) {
                continue;
            }
            listDto.add(convertirADto(entity));
        }
        return listDto;
    }

    //GETBYID
    public MovimientosDto obtenerMovimiento(Long id) {
        MovimientosEntity entity = movimientosRepo.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return convertirADto(entity);
    }

    //PUT
    public MovimientosDto modificarMovimiento(Long id, MovimientosDto dto) {
        MovimientosEntity entity = movimientosRepo.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        buscarCuentaActiva(entity.getCuenta().getIdCuenta());
        CuentaEntity cuenta = buscarCuentaActiva(dto.getIdCuenta());
        llenarEntity(entity, dto, cuenta);
        MovimientosEntity actualizado = movimientosRepo.save(entity);
        return convertirADto(actualizado);
    }

    //DELETE
    public void eliminarMovimiento(Long id) {
        MovimientosEntity entity = movimientosRepo.findById(id).orElse(null);
        if (entity != null) {
            buscarCuentaActiva(entity.getCuenta().getIdCuenta());
            movimientosRepo.delete(entity);
        }
    }
}
