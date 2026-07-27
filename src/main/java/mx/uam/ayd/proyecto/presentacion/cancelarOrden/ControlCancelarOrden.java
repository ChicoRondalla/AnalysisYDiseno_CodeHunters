package mx.uam.ayd.proyecto.presentacion.cancelarOrden;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;

@Component // EVITA HACER EL NEW
public class ControlCancelarOrden {

    @Autowired // AUTO CABLEADO
    private ServicioPedido servicioPedido;

    @Autowired
    private VistaCancelarOrden ventana;

    // INYECTA LA PANTALLA PRINCIPAL
    @Autowired
    @Lazy // NO CREA EL CONTROL DE INMEDIATO
    private ControlEnviarOrdenCocina controlEnviarOrdenCocina;

    // INICIA Y MUESTRA VENTANA 
    public void inicia(long idPedido) {
        ventana.muestra(this, idPedido);
    }

    /**
     * Método invocado por la ventana cuando el recepcionista confirma la cancelación.
     * 
     * @param idPedido ID del pedido a cancelar.
     * @param motivo Motivo ingresado por el recepcionista.
     */
    public void cancelarPedido(long idPedido, String motivo) {
        
        try {
            
            String usuarioActual = "Recepcionista en turno"; 
            
            // LlAMA AL SERVICIO QUE YA TIENE (RN-06, RN-07)
            boolean exito = servicioPedido.cancelarPedido(idPedido, motivo, usuarioActual);
            
            if (exito) {
                ventana.muestraMensajeExito("El pedido #" + idPedido + " ha sido cancelado exitosamente.");
                
                // LIMPIA LA TABLA 
                controlEnviarOrdenCocina.limpiarVistaDespuesDeCancelar();
                
                ventana.cierra();
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Si HAY UN ERRORES SE MUESTRAN EN PANTALLA
            ventana.muestraMensajeError(e.getMessage());
        } catch (Exception e) {
            ventana.muestraMensajeError("Ocurrió un error inesperado al cancelar el pedido.");
        }
    }
}
