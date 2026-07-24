package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;

public interface PlatilloRepository extends CrudRepository<Platillo, Long> {
    
    List<Platillo> findByTipoArea(String tipoArea);
    
    // Busca coincidencias en el nombre sin importar mayúsculas o minúsculas
    List<Platillo> findByNombreContainingIgnoreCase(String nombre);
    
    // Busca por el precio exacto
    List<Platillo> findByPrecio(int precio);
}