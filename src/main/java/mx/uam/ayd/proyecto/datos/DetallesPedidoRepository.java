package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;

/**
 * Repositorio para gestionar el acceso a datos de la entidad DetallesPedido.
 */
public interface DetallesPedidoRepository extends CrudRepository<DetallesPedido, Long> {

}
