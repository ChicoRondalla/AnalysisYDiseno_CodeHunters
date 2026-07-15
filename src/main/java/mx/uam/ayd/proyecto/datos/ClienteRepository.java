package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Repositorio para gestionar el acceso a datos de la entidad Cliente.
 */
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

}
