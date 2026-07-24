package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

public interface DetallesPedidoRepository extends CrudRepository<DetallesPedido, Long> {
    
    // Ahora Java ya sabe qué es un List y qué es un Pedido
    List<DetallesPedido> findByPedido(Pedido pedido);
    
}