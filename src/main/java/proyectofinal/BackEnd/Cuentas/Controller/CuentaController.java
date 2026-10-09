package proyectofinal.BackEnd.Cuentas.Controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import proyectofinal.BackEnd.ApiResponse;
import proyectofinal.BackEnd.Cuentas.Service.CuentaService;
import proyectofinal.BackEnd.Cuentas.models.DTO.CuentaDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaDto>> nuevaCuenta(@RequestBody @Valid CuentaDto json, BindingResult result) {
        try {
            if (result.hasErrors()) {
                String mensaje = result.getFieldError().getDefaultMessage();
                ApiResponse<CuentaDto> badRequest = new ApiResponse<>(false, mensaje);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
            }
            CuentaDto dto = service.crearCuenta(json);
            if (dto != null) {
                ApiResponse<CuentaDto> response = new ApiResponse<>(true, "Cuenta creada correctamente", dto);
                log.info("Cuenta creada correctamente");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                ApiResponse<CuentaDto> notFound = new ApiResponse<>(false, "No existe el usuario con ID " + json.getIdUsuario());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<CuentaDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            ApiResponse<CuentaDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<CuentaDto> responseError = new ApiResponse<>(false, "Error al crear la cuenta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<List<CuentaDto>>> obtenerCuentasDeUsuario(@PathVariable Long idUsuario) {
        try {
            List<CuentaDto> list = service.listarPorUsuario(idUsuario);
            if (list != null) {
                ApiResponse<List<CuentaDto>> response = new ApiResponse<>(true, "Cuentas listadas correctamente", list);
                log.info("Cuentas del usuario " + idUsuario + " listadas correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<List<CuentaDto>> notFound = new ApiResponse<>(false, "No existe el usuario con ID " + idUsuario);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<List<CuentaDto>> responseError = new ApiResponse<>(false, "No se pudo listar las cuentas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDto>> obtenerCuenta(@PathVariable Long id) {
        try {
            CuentaDto dto = service.obtenerCuenta(id);
            if (dto != null) {
                ApiResponse<CuentaDto> response = new ApiResponse<>(true, "Cuenta obtenida correctamente", dto);
                log.info("Cuenta con ID " + id + " obtenida correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<CuentaDto> notFound = new ApiResponse<>(false, "No existe la cuenta con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<CuentaDto> responseError = new ApiResponse<>(false, "No se pudo obtener la cuenta con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDto>> actualizarCuenta(@PathVariable Long id, @RequestBody @Valid CuentaDto json, BindingResult result) {
        try {
            if (result.hasErrors()) {
                String mensaje = result.getFieldError().getDefaultMessage();
                ApiResponse<CuentaDto> badRequest = new ApiResponse<>(false, mensaje);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
            }
            CuentaDto updated = service.modificarCuenta(id, json);
            if (updated != null) {
                ApiResponse<CuentaDto> response = new ApiResponse<>(true, "Cuenta actualizada correctamente", updated);
                log.info("La cuenta con ID " + id + " fue actualizada correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<CuentaDto> notFound = new ApiResponse<>(false, "No existe la cuenta con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<CuentaDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<CuentaDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<CuentaDto> responseError = new ApiResponse<>(false, "No se pudo actualizar la cuenta con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<CuentaDto>> desactivarCuenta(@PathVariable Long id) {
        try {
            CuentaDto updated = service.desactivarCuenta(id);
            if (updated != null) {
                ApiResponse<CuentaDto> response = new ApiResponse<>(true, "Cuenta desactivada correctamente", updated);
                log.info("La cuenta con ID " + id + " fue desactivada correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<CuentaDto> notFound = new ApiResponse<>(false, "No existe la cuenta con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<CuentaDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<CuentaDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<CuentaDto> responseError = new ApiResponse<>(false, "No se pudo desactivar la cuenta con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDto>> eliminarCuenta(@PathVariable Long id) {
        try {
            CuentaDto dto = service.obtenerCuenta(id);
            if (dto != null) {
                service.eliminarCuenta(id);
                ApiResponse<CuentaDto> response = new ApiResponse<>(true, "Cuenta eliminada correctamente");
                log.info("Cuenta con ID " + id + " eliminada correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<CuentaDto> notFound = new ApiResponse<>(false, "No existe la cuenta con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<CuentaDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<CuentaDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<CuentaDto> responseError = new ApiResponse<>(false, "No se pudo eliminar la cuenta con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }
}
