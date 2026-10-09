package proyectofinal.BackEnd.Usuarios.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyectofinal.BackEnd.ApiResponse;
import proyectofinal.BackEnd.Usuarios.Service.UsuariosService;
import proyectofinal.BackEnd.Usuarios.models.DTO.UsuariosDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuariosService service;

    public UsuariosController(UsuariosService service) {
        this.service = service;
    }

    //GET
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuariosDto>>> obtenerUsuarios() {
        try {
            List<UsuariosDto> list = service.listarUsuarios();
            ApiResponse<List<UsuariosDto>> response = new ApiResponse<>(true, "Usuarios listados correctamente", list);
            log.info("Usuarios listados correctamente");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<List<UsuariosDto>> responseError = new ApiResponse<>(false, "No se pudo listar los usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    //GETBYID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuariosDto>> obtenerUsuario(@PathVariable Long id) {
        try {
            UsuariosDto dto = service.obtenerUsuario(id);
            if (dto != null) {
                ApiResponse<UsuariosDto> response = new ApiResponse<>(true, "Usuario obtenido correctamente", dto);
                log.info("Usuario con ID " + id + " obtenido correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<UsuariosDto> notFound = new ApiResponse<>(false, "No existe el usuario con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<UsuariosDto> responseError = new ApiResponse<>(false, "No se pudo obtener el usuario con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuariosDto>> eliminarUsuario(@PathVariable Long id) {
        try {
            UsuariosDto dto = service.obtenerUsuario(id);
            if (dto != null) {
                service.eliminarUsuario(id);
                ApiResponse<UsuariosDto> response = new ApiResponse<>(true, "Usuario eliminado correctamente");
                log.info("Usuario con ID " + id + " eliminado correctamente");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ApiResponse<UsuariosDto> notFound = new ApiResponse<>(false, "No existe el usuario con ID " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<UsuariosDto> badRequest = new ApiResponse<>(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequest);
        } catch (DataIntegrityViolationException e) {
            //Violacion de llaves foraneas u otras restricciones de la base de datos
            ApiResponse<UsuariosDto> integridad = new ApiResponse<>(false, "La operacion no cumple una restriccion de la base de datos (llave foranea o valor no permitido)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(integridad);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<UsuariosDto> responseError = new ApiResponse<>(false, "No se pudo eliminar el usuario con ID " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
        }
    }
}
