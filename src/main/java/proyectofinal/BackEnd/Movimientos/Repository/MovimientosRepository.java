package proyectofinal.BackEnd.Movimientos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyectofinal.BackEnd.Movimientos.models.Entity.MovimientosEntity;

import java.util.List;

@Repository
public interface MovimientosRepository extends JpaRepository<MovimientosEntity, Long> {
    //Movimientos de una cuenta
    List<MovimientosEntity> findByCuentaIdCuenta(Long idCuenta);
}
