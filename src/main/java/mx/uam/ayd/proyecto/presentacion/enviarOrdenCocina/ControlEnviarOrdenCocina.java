package mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;

@Component

public class ControlEnviarOrdenCocina {

    // INYECTA EL SERVICIO DE PEDIDO PARA USAR SUS MÉTODOS
    @Autowired
    private ServicioPedido servicioPedido;

    // --- ENLACES CON EL ARCHIVO FXML ---
    
    @FXML
    private Label lblCliente;

    @FXML
    private Label lblMesa;

    @FXML
    private Label lblEstado;

    @FXML
    private TableView<?> tablaDetalle; 

    @FXML
    private Button btnEnviarCocina;

    // VARIABLE PARA GUARDAR EL ID DEL PEDIDO ACTUAL 
    private long idPedidoActual;

    // INYECTA LA VENTANA 
    @Autowired
    private VentanaEnviarOrdenCocina ventana;

    /**
     * METODO QUE INICIA EL CONTROLADOR CON EL ID DEL PEDIDO 
     * 
     * @param idPedido ID DEL PEDIDO 
     */
    public void inicia(long idPedido) {
        this.idPedidoActual = idPedido;

        // HACE VISIBLE LA VENTANA
        ventana.muestra();
    }

    /**
     * PERMITE INICIAR CON EL EVENTO CON UN CLIC
     * 
     * @param event 
     */
    @FXML
    void clickBotonEnviar(ActionEvent event) {
        try {
            // REGLA DE NEGOCIO (RN-05)
            boolean exito = servicioPedido.procesarEnvioCocina(idPedidoActual);
            
            if (exito) {
                // ACTUALIZA EL ESTADO EN LA INTERFAZ 
                lblEstado.setText("En Preparación");
                
                // MUESTRA UN MENSAJE DE ÉXITO 
                mostrarMensajeExito();
            }
            // SI NO ENCUENTRA EL PEDIDO MUESTRA UN MENSAJE DE ERROR
        }  catch (IllegalArgumentException e) {

                mostrarMensajeError("No se encontró el pedido", e.getMessage());

            
        } catch (IllegalStateException e) {
            // POR SI EL PEDIDO YA ESTÁ EN PREPARACIÓN, SE MUESTRA UN MENSAJE DE ERROR
            mostrarMensajeError("La orden ya está en preparación y no puede ser modificada", e.getMessage());
        }
    }
    
     // MUESTRA QUE LA ORDEN SE ENVIO CORRECTAMENTE 
    private void mostrarMensajeExito() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("¡La orden ha sido enviada exitosamente a la cocina!");
        alert.showAndWait();
    }

     // MUESTRA UN MENSAJE DE ERROR
    private void mostrarMensajeError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
