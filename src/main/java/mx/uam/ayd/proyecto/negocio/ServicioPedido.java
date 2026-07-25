package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
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
        
        // Verificamos que no sea nulo y contenga EXACTAMENTE 10 números (cero letras)
        if (telefono == null || !telefono.matches("\\d{10}")) return false;
        
        if (direccion == null || direccion.trim().isEmpty()) return false;
        
        return true;
    }

    /**
     * Crea un nuevo cliente y su pedido asociado para entrega a domicilio.
     */
    public Pedido crearPedidoDomicilio(String nombre, String telefono, String direccion) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente = clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setTipoOrden("Domicilio");
        pedido.setEstado("Pendiente");
        pedido.setNumeroOrden((int) (Math.random() * 10000)); 
        pedido.setCliente(cliente);
        
        return pedidoRepository.save(pedido);
    }

    /**
     * Crea un nuevo cliente y su pedido asociado para recoger en sucursal.
     */
    public Pedido crearPedidoRecoger(String nombre, String telefono) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        
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

    /**
     * Crea un pedido para consumo local asignado a una mesa específica.
     */
    public Pedido crearPedidoLocal(int numeroMesa) {
        Cliente cliente = new Cliente();
        cliente.setNombre("Mesa " + numeroMesa);
        cliente = clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setTipoOrden("Local");
        pedido.setEstado("Pendiente");
        pedido.setNumeroOrden((int) (Math.random() * 10000));
        pedido.setCliente(cliente);

        return pedidoRepository.save(pedido);
    }

    /**
     * Recupera un pedido por su ID cargando sus datos y detalles.
     */
    @Transactional(readOnly = true)
    public Pedido recuperaPedido(long idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            // Carga forzada de la colección Lazy de detalles
            if (pedido.getDetallesPedido() != null) {
                pedido.getDetallesPedido().size(); 
            }
            return pedido;
        }
        return null;
    }

    /**
     * RECUPERA PEDIDOS PENDIENTES / EN PREPARACIÓN PARA LA COMANDA DE COCINA.
     */
    @Transactional(readOnly = true)
    public List<Pedido> recuperaPedidosPendientes() {
        List<Pedido> pedidosPendientes = new ArrayList<>();
        
        // Recuperamos los pedidos activos desde el repositorio
        Iterable<Pedido> todosLosPedidos = pedidoRepository.findAll();
        
        for (Pedido pedido : todosLosPedidos) {
            // Filtramos aquellos cuya orden esté en preparación o pendiente
            if ("En Preparación".equalsIgnoreCase(pedido.getEstado()) || "Pendiente".equalsIgnoreCase(pedido.getEstado())) {
                // Forzamos la carga de la lista Lazy de detalles dentro de la transacción
                if (pedido.getDetallesPedido() != null) {
                    pedido.getDetallesPedido().size();
                }
                pedidosPendientes.add(pedido);
            }
        }
        
        return pedidosPendientes;
    }

    /**
     * RECUPERA PEDIDOS COMPLETADOS / DESPACHADOS DE COCINA.
     */
    @Transactional(readOnly = true)
    public List<Pedido> recuperaPedidosCompletados() {
        List<Pedido> completados = new ArrayList<>();
        Iterable<Pedido> todosLosPedidos = pedidoRepository.findAll();
        
        for (Pedido pedido : todosLosPedidos) {
            if ("Completado".equalsIgnoreCase(pedido.getEstado())) {
                if (pedido.getDetallesPedido() != null) {
                    pedido.getDetallesPedido().size(); // Carga Lazy
                }
                completados.add(pedido);
            }
        }
        
        return completados;
    }

    /**
     * HU-03: ENVIO A COCINA.
     */
    @Transactional
    public boolean procesarEnvioCocina(long idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el pedido con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoOpt.get();

        if ("En Preparación".equals(pedido.getEstado())) {
            throw new IllegalStateException("La orden ya fue enviada a cocina y no puede ser modificada.");
        }

        List<DetallesPedido> detalles = pedido.getDetallesPedido();
        
        List<DetallesPedido> paraPlancha = new ArrayList<>();
        List<DetallesPedido> paraRollos = new ArrayList<>();

        if (detalles != null) {
            for (DetallesPedido detalle : detalles) {
                if (detalle.getPlatillo() != null) {
                    String area = detalle.getPlatillo().getTipoArea();
                    
                    if ("Plancha".equalsIgnoreCase(area)) {
                        paraPlancha.add(detalle);
                    } else if ("Rollos".equalsIgnoreCase(area)) {
                        paraRollos.add(detalle);
                    }
                }
            }
        }

        enviarAPlancha(paraPlancha);
        enviarARollos(paraRollos);

        pedido.setEstado("En Preparación");
        pedidoRepository.save(pedido);

        return true; 
    }

    private void enviarAPlancha(List<DetallesPedido> detalles) {
        if (!detalles.isEmpty()) {
            System.out.println("-> Enviando " + detalles.size() + " platillos a PLANCHA.");
        }
    }

    private void enviarARollos(List<DetallesPedido> detalles) {
        if (!detalles.isEmpty()) {
            System.out.println("-> Enviando " + detalles.size() + " platillos a ROLLOS.");
        }
    }

    /**
     * HU-04: CANCELA UN PEDIDO.
     */
    @Transactional
    public boolean cancelarPedido(long idPedido, String motivoCancelacion, String idUsuario) {
        if (motivoCancelacion == null || motivoCancelacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de cancelación es obligatorio.");
        }

        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el pedido con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoOpt.get();

        pedido.setEstado("Cancelada");
        String detalleCancelacion = motivoCancelacion.trim() + " (Cancelado por: " + idUsuario + ")";
        pedido.setMotivoCancelacion(detalleCancelacion);

        notificarCancelacionCocina(pedido.getIdPedido());
        pedidoRepository.save(pedido);

        return true; 
    }

    private void notificarCancelacionCocina(long idPedido) {
        System.err.println("-> [ALERTA COCINA - STOP] DETENER PREPARACIÓN: El pedido ID " + idPedido + " ha sido CANCELADO.");
    }
}