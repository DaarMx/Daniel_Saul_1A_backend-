package proyectofinal.BackEnd.Usuarios.Service;

import org.springframework.stereotype.Service;
import proyectofinal.BackEnd.Cuentas.Repository.CuentaRepository;
import proyectofinal.BackEnd.Cuentas.Service.CuentaService;
import proyectofinal.BackEnd.Cuentas.models.Entity.CuentaEntity;
import proyectofinal.BackEnd.Usuarios.Repository.UsuariosRepository;
import proyectofinal.BackEnd.Usuarios.models.DTO.UsuariosDto;
import proyectofinal.BackEnd.Usuarios.models.Entity.UsuariosEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepo;
    private final CuentaRepository cuentaRepo;
    private final CuentaService cuentaService;

    public UsuariosService(UsuariosRepository usuariosRepo, CuentaRepository cuentaRepo, CuentaService cuentaService) {
        this.usuariosRepo = usuariosRepo;
        this.cuentaRepo = cuentaRepo;
        this.cuentaService = cuentaService;
    }

    private UsuariosDto convertirADto(UsuariosEntity entity) {
        UsuariosDto dto = new UsuariosDto();
        dto.setIdUsuario(entity.getIdUsuario());
        dto.setNombre(entity.getNombre());
        dto.setCorreo(entity.getCorreo());
        dto.setFechaRegistro(entity.getFechaRegistro());
        return dto;
    }

    //GET
    public List<UsuariosDto> listarUsuarios() {
        List<UsuariosEntity> list = usuariosRepo.findAll();
        List<UsuariosDto> listDto = new ArrayList<>();
        for (UsuariosEntity entity : list) {
            listDto.add(convertirADto(entity));
        }
        return listDto;
    }

    //GETBYID (incluye las cuentas del usuario)
    public UsuariosDto obtenerUsuario(Long id) {
        UsuariosEntity entity = usuariosRepo.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        UsuariosDto dto = convertirADto(entity);
        dto.setCuentas(cuentaService.listarPorUsuario(id));
        return dto;
    }

    //DELETE (solo si el usuario no tiene cuentas)
    public void eliminarUsuario(Long id) {
        List<CuentaEntity> cuentas = cuentaRepo.findByUsuarioIdUsuario(id);
        if (!cuentas.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el usuario porque posee cuentas registradas");
        }
        usuariosRepo.deleteById(id);
    }
}
