package mx.uam.ayd.proyecto.negocio;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;


/**
 * Lógica de negocio para la gestión de Pedidos
 */
@Service
public class ServicioPedido {
// INJECTA EL REPOSITORIO DE PEDIDO PARA USAR SUS MÉTODOS
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
    // TERMINA HU

    /**
     * INICIA HU-03
     * ENVIO A COCINA  (HU-03).
     * @param idPedido IDENTIFICADOR DEL PEDIDO.
     * @return TRUE SI PROCESA CORRECTAMENTE.
     */
    public boolean procesarEnvioCocina(long idPedido) {
        // 1. RECUPERAR PEDIDO POR ID
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el pedido con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoOpt.get();

        // 2. RN-05: VALIDAR ESTADO DEL PEDIDO ANTES DE ENVIAR A COCINA
        if ("En Preparación".equals(pedido.getEstado())) {
            throw new IllegalStateException("La orden ya fue enviada a cocina y no puede ser modificada.");
        }

        // 3. OBTENEMOS LOS DETALLES DEL PEDIDO
        List<DetallesPedido> detalles = pedido.getDetallesPedido();
        
        // SEPARAN LOS DETALLES POR ÁREA DE COCINA
        List<DetallesPedido> paraPlancha = new ArrayList<>();
        List<DetallesPedido> paraRollos = new ArrayList<>();

        // RN-04: DIVISION DE COMANDAS
        for (DetallesPedido detalle : detalles) {
            String area = detalle.getPlatillo().getTipoArea();
            
            if ("Plancha".equalsIgnoreCase(area)) {
                paraPlancha.add(detalle);
            } else if ("Rollos".equalsIgnoreCase(area)) {
                paraRollos.add(detalle);
            }
        }

        // 4. ENVIAR A ESTACIONES DE COCINA 
        enviarAPlancha(paraPlancha);
        enviarARollos(paraRollos);

        // 5. CAMBIAR ESTADO DEL PEDIDO A "En Preparación" 
        pedido.setEstado("En Preparación");

        // 6. ACTUALIZAR EL PEDIDO EN LA BASE DE DATOS
        pedidoRepository.save(pedido);

        return true; 
    }

     //ENVIA DETALLES A PLANCHA 
     
    private void enviarAPlancha(List<DetallesPedido> detalles) {
        if (!detalles.isEmpty()) {
            System.out.println("-> Enviando " + detalles.size() + " platillos a PLANCHA.");
        }
    }

     //ENVIA DETALLES A ROLLOS 

    private void enviarARollos(List<DetallesPedido> detalles) {
        if (!detalles.isEmpty()) {
            System.out.println("-> Enviando " + detalles.size() + " platillos a ROLLOS.");
        }
    }
    // TERMINA HU-03
}
