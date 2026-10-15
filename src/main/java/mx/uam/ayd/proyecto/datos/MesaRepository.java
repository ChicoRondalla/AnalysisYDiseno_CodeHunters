package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Mesa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de datos para la entidad Mesa (Tarea 2 - HU-05).
 * Proporciona métodos de persistencia y consultas a la base de datos.
 */
@Repository
public interface MesaRepository extends CrudRepository<Mesa, Long> {

    /**
     * Busca una mesa por su número identificador único en el restaurante.
     */
    Optional<Mesa> findByNumeroMesa(int numeroMesa);

    /**
     * Obtiene una lista de todas las mesas filtradas por su estado actual (ej. "LIBRE" u "OCUPADA").
     */
    List<Mesa> findByEstado(String estado);
}