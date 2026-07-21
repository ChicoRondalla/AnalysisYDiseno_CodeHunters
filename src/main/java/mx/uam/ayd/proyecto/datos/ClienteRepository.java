package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente
 */
public interface ClienteRepository extends CrudRepository<Cliente, Long> {
    
    /**
     * Busca un cliente en la base de datos usando su número de teléfono.
     * Retorna un Optional por si el cliente es nuevo y no existe aún.
     */
    Optional<Cliente> findByTelefono(String telefono);
}