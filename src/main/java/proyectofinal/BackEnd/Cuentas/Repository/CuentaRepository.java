package proyectofinal.BackEnd.Cuentas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyectofinal.BackEnd.Cuentas.models.Entity.CuentaEntity;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {
    List<CuentaEntity> findByUsuarioIdUsuario(Long idUsuario);
}
