package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

/**
 * Lógica de negocio para la gestión de Pedidos
 */
@Service
public class ServicioPedido {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Valida que los datos del formulario a domicilio no estén vacíos
     * y que el teléfono tenga la longitud correcta (RN-02).
     */
    public boolean validarDatosDomicilio(String nombre, String telefono, String direccion) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (telefono == null || telefono.trim().length() != 10) return false;
        if (direccion == null || direccion.trim().isEmpty()) return false;
        
        return true;
    }

    /**
     * Crea un nuevo cliente y su pedido asociado para entrega a domicilio.
     */
    public Pedido crearPedidoDomicilio(String nombre, String telefono, String direccion) {
        // 1. Creamos y guardamos al Cliente en la base de datos
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente = clienteRepository.save(cliente);

        // 2. Creamos el Pedido y le asignamos sus valores iniciales
        Pedido pedido = new Pedido();
        pedido.setTipoOrden("Domicilio");
        pedido.setEstado("Pendiente");
        
        // Generamos un número de orden temporal (pueden cambiar esta lógica después)
        pedido.setNumeroOrden((int) (Math.random() * 10000)); 
        
        // 3. Vinculamos el cliente al pedido y guardamos el pedido
        pedido.setCliente(cliente);
        return pedidoRepository.save(pedido);
    }

    /**
     * Crea un nuevo cliente y su pedido asociado para recoger en sucursal.
     */
    public Pedido crearPedidoRecoger(String nombre, String telefono) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        
        // El teléfono es opcional para recoger
        if (telefono != null && !telefono.trim().isEmpty()) {
            cliente.setTelefono(telefono);
        }
        cliente = clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setTipoOrden("Recoger");
        pedido.setEstado("Pendiente");
        pedido.setNumeroOrden((int) (Math.random() * 10000));
        pedido.setCliente(cliente);

        return pedidoRepository.save(pedido);
    }
}