package mx.uam.ayd.proyecto.presentacion.cancelarOrden;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Ventana para el caso de uso: Cancelar Orden (HU-04)
 */
@Component
public class VistaCancelarOrden {

    private ControlCancelarOrden control;
    private long idPedidoActual; // Guardamos el ID del pedido que estamos editando

    // --- CONEXIÓN CON LOS fx:id DEL ARCHIVO FXML ---
    @FXML private Label mensajeOrdenLabel;
    @FXML private TextArea motivoArea;
    @FXML private Button volverBtn;
    @FXML private Button confirmarBtn;
    @FXML private Label charCountLabel; // Opcional, por si luego le quieres poner lógica del contador

    /**
     * Este método se ejecuta automáticamente cuando JavaFX carga la vista.
     */
    @FXML
    public void initialize() {
        // ¿Qué pasa al darle clic al botón Volver?
        volverBtn.setOnAction(event -> cierra());

        // ¿Qué pasa al darle clic al botón Confirmar Cancelación?
        confirmarBtn.setOnAction(event -> {
            String motivo = motivoArea.getText();
            // ¡Llamamos a nuestro controlador que a su vez llama al servicio!
            control.cancelarPedido(idPedidoActual, motivo);
        });
    }

    /**
     * Muestra la ventana y prepara los datos.
     */
    public void muestra(ControlCancelarOrden control, long idPedido) {
        this.control = control;
        this.idPedidoActual = idPedido;

        // Actualizamos el mensaje visual con el número de orden real
        if (mensajeOrdenLabel != null) {
            mensajeOrdenLabel.setText("Estás a punto de cancelar la orden #" + idPedido + ".");
        }
        
        // Limpiamos el área de texto por si quedó algo escrito de antes
        if (motivoArea != null) {
            motivoArea.clear();
        }

        // TODO: Aquí va tu lógica habitual para mostrar el Stage de JavaFX
        System.out.println("Cargando y mostrando FXML de Cancelación para orden #" + idPedido);
    }

    /**
     * Muestra un mensaje de éxito al usuario.
     */
    public void muestraMensajeExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Cancelación Exitosa");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de error al usuario.
     */
    public void muestraMensajeError(String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error al cancelar");
        alerta.setHeaderText("No se pudo completar la acción");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Cierra la ventana actual.
     */
    public void cierra() {
        // Obtenemos la ventana actual a partir del botón y la cerramos
        if (volverBtn != null && volverBtn.getScene() != null) {
            Stage stage = (Stage) volverBtn.getScene().getWindow();
            stage.close();
        }
    }
}