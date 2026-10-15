package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Mesa;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

/**
 * Repositorio de datos para la entidad Mesa.
 */
public interface MesaRepository extends CrudRepository<Mesa, Long> {

    /**
     * Busca una mesa por su número identificador (ej. Mesa 1, Mesa 2).
     */
    Optional<Mesa> findByNumeroMesa(int numeroMesa);
}