package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;

/**
 * Repositorio para la entidad Platillo.
 * Spring Boot se encarga de implementar automáticamente los métodos CRUD (guardar, buscar, eliminar, etc.)
 */
public interface PlatilloRepository extends CrudRepository<Platillo, Long> {
    
    // Si más adelante la HU-02 requiere buscar solo "Bebidas" o "Rollos", 
    // podemos agregar métodos personalizados aquí, por ejemplo:
    // Iterable<Platillo> findByTipoArea(String tipoArea);

}
