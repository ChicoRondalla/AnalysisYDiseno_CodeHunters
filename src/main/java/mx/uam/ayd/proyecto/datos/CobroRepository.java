package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cobro;

/**
 * Repositorio para gestionar el acceso a datos de la entidad Cobro.
 */
public interface CobroRepository extends CrudRepository<Cobro, Long> {

}
