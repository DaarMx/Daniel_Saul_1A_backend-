package proyectofinal.BackEnd.Movimientos.Controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import proyectofinal.BackEnd.ApiResponse;
import proyectofinal.BackEnd.Movimientos.Service.MovimientosService;
import proyectofinal.BackEnd.Movimientos.models.DTO.MovimientosDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/movimientos")
public class MovimientosController {

    private final MovimientosService service;

    public MovimientosController(MovimientosService service) {
        this.service = service;
    }

    //POST
    @PostMapping
    public ResponseEntity<ApiResponse<MovimientosDto>> nuevoMovimiento(@RequestBody @Valid MovimientosDto json, BindingResult result) {
        try {
            if (result.hasErrors()) {
                String mensaje = result.getFieldError().getDefaultMessage();
                ApiResponse<MovimientosDto> badRequest = new ApiResponse<>(false, mensaje);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
            }
            MovimientosDto dto = service.crearMovimiento(json);
            ApiResponse<MovimientosDto> response = new ApiResponse<>(true, "Movimiento registrado correctamente", dto);
            log.info("Movimiento registrado correctamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<MovimientosDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<MovimientosDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<MovimientosDto> responseError = new ApiResponse<>(false, "Error al registrar el movimiento");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    //GET (movimientos de una cuenta) Filtros opcionales: ?tipo=&categoria=&desde=&hasta=
    @GetMapping("/cuenta/{idCuenta}")
    public ResponseEntity<ApiResponse<List<MovimientosDto>>> obtenerMovimientosDeCuenta(
            @PathVariable Long idCuenta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        try {
            List<MovimientosDto> list = service.listarPorCuenta(idCuenta, tipo, categoria, desde, hasta);
            if (list != null) {
                ApiResponse<List<MovimientosDto>> response = new ApiResponse<>(true, "Movimientos listados correctamente", list);
                log.info("Movimientos de la cuenta " + idCuenta + " listados correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<List<MovimientosDto>> notFound = new ApiResponse<>(false, "No existe la cuenta con ID " + idCuenta);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<List<MovimientosDto>> responseError = new ApiResponse<>(false, "No se pudo listar los movimientos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    //GETBYID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientosDto>> obtenerMovimiento(@PathVariable Long id) {
        try {
            MovimientosDto dto = service.obtenerMovimiento(id);
            if (dto != null) {
                ApiResponse<MovimientosDto> response = new ApiResponse<>(true, "Movimiento obtenido correctamente", dto);
                log.info("Movimiento con ID " + id + " obtenido correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<MovimientosDto> notFound = new ApiResponse<>(false, "No existe el movimiento con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<MovimientosDto> responseError = new ApiResponse<>(false, "No se pudo obtener el movimiento con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    //PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientosDto>> actualizarMovimiento(@PathVariable Long id, @RequestBody @Valid MovimientosDto json, BindingResult result) {
        try {
            if (result.hasErrors()) {
                String mensaje = result.getFieldError().getDefaultMessage();
                ApiResponse<MovimientosDto> badRequest = new ApiResponse<>(false, mensaje);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
            }
            MovimientosDto updated = service.modificarMovimiento(id, json);
            if (updated != null) {
                ApiResponse<MovimientosDto> response = new ApiResponse<>(true, "Movimiento actualizado correctamente", updated);
                log.info("El movimiento con ID " + id + " fue actualizado correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<MovimientosDto> notFound = new ApiResponse<>(false, "No existe el movimiento con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<MovimientosDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<MovimientosDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<MovimientosDto> responseError = new ApiResponse<>(false, "No se pudo actualizar el movimiento con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientosDto>> eliminarMovimiento(@PathVariable Long id) {
        try {
            MovimientosDto dto = service.obtenerMovimiento(id);
            if (dto != null) {
                service.eliminarMovimiento(id);
                ApiResponse<MovimientosDto> response = new ApiResponse<>(true, "Movimiento eliminado correctamente");
                log.info("Movimiento con ID " + id + " eliminado correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<MovimientosDto> notFound = new ApiResponse<>(false, "No existe el movimiento con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<MovimientosDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<MovimientosDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<MovimientosDto> responseError = new ApiResponse<>(false, "No se pudo eliminar el movimiento con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }
}
