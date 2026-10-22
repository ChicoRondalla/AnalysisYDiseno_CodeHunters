package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Empleado;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * Repositorio de datos para la gestion y persistencia de la entidad Empleado.
 * Utiliza ListCrudRepository para obtener colecciones de tipo List directamente.
 * 
 * Cumple con DoD #5 (Documentacion de codigo).
 */
public interface EmpleadoRepository extends ListCrudRepository<Empleado, Long> {

    /**
     * Busca un empleado a partir de su nombre de usuario.
     * 
     * @param usuario Nombre de usuario unico a buscar.
     * @return Un {@link Optional} que contiene el empleado si existe, o vacio si no.
     */
    Optional<Empleado> findByUsuario(String usuario);

    /**
     * Verifica si ya existe un empleado registrado con el nombre de usuario proporcionado.
     * 
     * @param usuario Nombre de usuario a comprobar.
     * @return true si el usuario ya existe, false en caso contrario.
     */
    boolean existsByUsuario(String usuario);
}