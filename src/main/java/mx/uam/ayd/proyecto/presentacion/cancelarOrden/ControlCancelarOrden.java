package mx.uam.ayd.proyecto.presentacion.cancelarOrden;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;

@Component
public class ControlCancelarOrden {

    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    private VistaCancelarOrden ventana;

    // ---> INYECTAMOS TU PANTALLA PRINCIPAL <---
    @Autowired
    @Lazy
    private ControlEnviarOrdenCocina controlEnviarOrdenCocina;

    /**
     * Inicia el flujo de cancelar orden, abriendo la ventana.
     * @param idPedido El ID de la orden que se quiere cancelar.
     */
    public void inicia(long idPedido) {
        // Le pasamos el control a la ventana para que se muestre
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
            // Simulamos obtener el usuario actual (puedes adaptarlo si ya tienes sesión de usuarios)
            String usuarioActual = "Recepcionista en turno"; 
            
            // Llamamos a nuestro servicio que ya tiene las Reglas de Negocio (RN-06, RN-07)
            boolean exito = servicioPedido.cancelarPedido(idPedido, motivo, usuarioActual);
            
            if (exito) {
                ventana.muestraMensajeExito("El pedido #" + idPedido + " ha sido cancelado exitosamente.");
                
                // ---> ¡LA MAGIA! LIMPIAMOS LA TABLA Y CAMBIAMOS EL TEXTO <---
                controlEnviarOrdenCocina.limpiarVistaDespuesDeCancelar();
                
                ventana.cierra();
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Si el servicio lanza un error (ej. motivo vacío o estado inválido), se lo mostramos a la ventana
            ventana.muestraMensajeError(e.getMessage());
        } catch (Exception e) {
            ventana.muestraMensajeError("Ocurrió un error inesperado al cancelar el pedido.");
        }
    }
}
