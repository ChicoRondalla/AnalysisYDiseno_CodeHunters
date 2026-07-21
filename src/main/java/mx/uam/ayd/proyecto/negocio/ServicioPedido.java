package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

@Service
public class ServicioPedido {
// INJECTA EL REPOSITORIO DE PEDIDO PARA USAR SUS MÉTODOS
    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Valida los datos de un pedido a domicilio según la RN-02.
     * 
     * @param nombre El nombre del cliente.
     * @param telefono El teléfono del cliente (debe tener exactamente 10 dígitos).
     * @param direccion La dirección de entrega (no puede estar vacía).
     * @return true si los datos son válidos, false si rompen alguna regla de negocio.
     */
    public boolean validarDatosDomicilio(String nombre, String telefono, String direccion) {
        // Validación de campos vacíos
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (direccion == null || direccion.trim().isEmpty()) return false;
        if (telefono == null || telefono.trim().isEmpty()) return false;

        // Validación estricta de 10 dígitos numéricos para el teléfono
        if (!telefono.matches("\\d{10}")) {
            return false;
        }

        // Si pasa todas las validaciones
        return true;
    }


    
    /**
     * INICIA HU-03
     * ENVIO A COSINA  (HU-03).
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

        // RN-04: DIVICION DE COMANDAS
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

