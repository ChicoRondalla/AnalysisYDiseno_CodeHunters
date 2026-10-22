package mx.uam.ayd.proyecto.presentacion.cancelarOrden;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;
import mx.uam.ayd.proyecto.presentacion.registrarPedido.ControlRegistroPedido; 

@Component // EVITA HACER EL NEW
public class ControlCancelarOrden {
 
    // SERVICIOS DE NEGOCIO Y VENTANA
    @Autowired 
    private ServicioPedido servicioPedido;

    @Autowired
    private VistaCancelarOrden ventana;

    // NAVEGACION ENTRE VENTANAS
    @Autowired
    @Lazy 
    private ControlEnviarOrdenCocina controlEnviarOrdenCocina;

    @Autowired
    @Lazy
    private ControlRegistroPedido controlRegistroPedido;

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
            
            // EJECUTA LA LOGICA DE NEGOCIO PARA CANCELAR EL PEDIDO
            boolean exito = servicioPedido.cancelarPedido(idPedido, motivo, usuarioActual);
            
            if (exito) {
                ventana.muestraMensajeExito("El pedido #" + idPedido + " ha sido cancelado exitosamente.");
                
                // LIMPIA LA TABLA 
                controlEnviarOrdenCocina.limpiarVistaDespuesDeCancelar();

                // CIERRA LA VENTANA DE CANCELACION
                ventana.cierra();
                
                // CIERRA TODAS LAS VENTANAS RELACIONADAS CON EL PEDIDO CANCELADO Y REGRESA A LA VENTANA DE REGISTRO DE PEDIDOS
                controlEnviarOrdenCocina.cerrarTodoPorCancelacion();
                controlRegistroPedido.inicia();
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Si HAY UN ERRORES SE MUESTRAN EN PANTALLA
            ventana.muestraMensajeError(e.getMessage());
        } catch (Exception e) {
            ventana.muestraMensajeError("Ocurrió un error inesperado al cancelar el pedido.");
        }
    }
}
