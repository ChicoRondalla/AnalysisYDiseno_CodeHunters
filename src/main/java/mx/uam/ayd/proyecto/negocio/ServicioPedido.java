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
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;

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
     * Genera un número de orden consecutivo automático y seguro.
     * Cuenta los registros existentes y le suma 1, asegurando que pase de #1 a #2, #3, etc.
     */
    private int generarSiguienteNumeroOrden() {
        long totalPedidos = pedidoRepository.count();
        return (int) (totalPedidos + 1);
    }

    /**
     * Valida que los datos del formulario a domicilio no estén vacíos
     * y que el teléfono tenga la longitud correcta (RN-02).
     */
    public boolean validarDatosDomicilio(String nombre, String telefono, String direccion) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (telefono == null || !telefono.matches("\\d{10}")) return false;
        if (direccion == null || direccion.trim().isEmpty()) return false;
        return true;
    }

    /**
     * Crea un nuevo cliente y su pedido asociado para entrega a domicilio con número automático.
     */
    @Transactional
    public Pedido crearPedidoDomicilio(String nombre, String telefono, String direccion) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente = clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setTipoOrden("Domicilio");
        pedido.setEstado("Pendiente");
        pedido.setNumeroOrden(generarSiguienteNumeroOrden()); // <--- Asigna el consecutivo automático aquí
        pedido.setCliente(cliente);
        
        return pedidoRepository.save(pedido);
    }

    /**
     * Crea un nuevo cliente y su pedido asociado para recoger en sucursal con número automático.
     */
    @Transactional
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
        pedido.setNumeroOrden(generarSiguienteNumeroOrden()); // <--- Asigna el consecutivo automático aquí
        pedido.setCliente(cliente);

        return pedidoRepository.save(pedido);
    }

    /**
     * Crea un pedido para consumo local asignado a una mesa específica con número automático.
     */
    @Transactional
    public Pedido crearPedidoLocal(int numeroMesa) {
        Cliente cliente = new Cliente();
        cliente.setNombre("Mesa " + numeroMesa);
        cliente = clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setTipoOrden("Local");
        pedido.setEstado("Pendiente");
        pedido.setNumeroOrden(generarSiguienteNumeroOrden()); // <--- Asigna el consecutivo automático aquí
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
            if (pedido.getDetallesPedido() != null) {
                pedido.getDetallesPedido().size(); 
            }
            return pedido;
        }
        return null;
    }

    /**
     * Recupera pedidos pendientes / en preparación para la comanda de cocina.
     */
    @Transactional(readOnly = true)
    public List<Pedido> recuperaPedidosPendientes() {
        List<Pedido> pedidosPendientes = new ArrayList<>();
        Iterable<Pedido> todosLosPedidos = pedidoRepository.findAll();
        
        for (Pedido pedido : todosLosPedidos) {
            if ("En Preparación".equalsIgnoreCase(pedido.getEstado()) || "Pendiente".equalsIgnoreCase(pedido.getEstado())) {
                if (pedido.getDetallesPedido() != null) {
                    pedido.getDetallesPedido().size();
                }
                pedidosPendientes.add(pedido);
            }
        }
        
        return pedidosPendientes;
    }

    /**
     * Recupera pedidos completados de cocina.
     */
    @Transactional(readOnly = true)
    public List<Pedido> recuperaPedidosCompletados() {
        List<Pedido> completados = new ArrayList<>();
        Iterable<Pedido> todosLosPedidos = pedidoRepository.findAll();
        
        for (Pedido pedido : todosLosPedidos) {
            if ("Completado".equalsIgnoreCase(pedido.getEstado())) {
                if (pedido.getDetallesPedido() != null) {
                    pedido.getDetallesPedido().size();
                }
                completados.add(pedido);
            }
        }
        
        return completados;
    }

    /**
     * Finaliza una orden introduciendo su número mediante el pad numérico de cocina.
     */
    @Transactional
    public boolean finalizarOrdenPorNumero(int numeroOrden) {
        List<Pedido> pendientes = recuperaPedidosPendientes();
        
        for (Pedido pedido : pendientes) {
            if (pedido.getNumeroOrden() == numeroOrden) {
                pedido.setEstado("Completado");
                pedidoRepository.save(pedido);
                return true;
            }
        }
        return false;
    }

    /**
     * Procesa el envío a cocina cambiando el estado.
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
        
        // ---> AGREGA ESTA NUEVA VALIDACIÓN <---
        if ("Cancelada".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("Esta orden ha sido cancelada y no puede enviarse a cocina.");
        }

        List<DetallesPedido> detalles = pedido.getDetallesPedido();
        
        // ... (El resto de tu código se queda igual) ...
        
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
        if (detalles != null && !detalles.isEmpty()) {
            System.out.println("-> Enviando " + detalles.size() + " platillos a PLANCHA.");
            // Aquí en el futuro puedes agregar la lógica real para mandar a la pantalla de cocina
        }
    }

    private void enviarARollos(List<DetallesPedido> detalles) {
        if (detalles != null && !detalles.isEmpty()) {
            System.out.println("-> Enviando " + detalles.size() + " platillos a ROLLOS.");
            // Aquí en el futuro puedes agregar la lógica real para mandar a la pantalla de cocina
        }
    }

    

    /**
     * Cancela un pedido.
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

        pedidoRepository.save(pedido);

        return true; 
    }
}