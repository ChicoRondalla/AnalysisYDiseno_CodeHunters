package mx.uam.ayd.proyecto.presentacion.cancelarOrden;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

@Component
public class VistaCancelarOrden {

    // INYECTA EL CONTROLADOR DE CANCELAR ORDEN
    @Autowired
    @Lazy
    private ControlCancelarOrden control;
    
    // INICIALIZA EL ID CON 1 
    private long idPedidoActual = 1L; 

    // --- CONEXIÓN CON LOS fx:id DEL ARCHIVO FXML ---
    @FXML private Label mensajeOrdenLabel;
    @FXML private TextArea motivoArea;
    @FXML private Button volverBtn;
    @FXML private Button confirmarBtn;
    @FXML private Label charCountLabel; 

    @FXML
    public void initialize() {
        // BOTON VOLVER
        volverBtn.setOnAction(event -> cierra());

        // BOTON CONFIRMAR
        confirmarBtn.setOnAction(event -> {
            String motivo = motivoArea.getText();
            
            System.out.println("¡Botón presionado!");
            System.out.println("Motivo escrito: " + motivo);
            
            // VALIDA POR SI NO ESCRIBEN NADA
            if(motivo == null || motivo.trim().isEmpty()) {
                muestraMensajeError("Debes escribir un motivo para cancelar la orden.");
                return;
            }

            // LLAMA AL CONTROLADOR 
            if(control != null) {
                System.out.println("Enviando orden de cancelación al controlador...");
                control.cancelarPedido(idPedidoActual, motivo);
            } else {
                System.out.println("Error: El controlador no se inyectó correctamente.");
            }
        });
    }

    public void muestra(ControlCancelarOrden control, long idPedido) {
        this.control = control;
        this.idPedidoActual = idPedido;
        // ADVERTENCIA CON EL ID ACTUAL
        if (mensajeOrdenLabel != null) {
            mensajeOrdenLabel.setText("Estás a punto de cancelar la orden #" + idPedido + ".");
        }
        // LIMPIA AREA DE TEXTO
        if (motivoArea != null) {
            motivoArea.clear();
        }
    }
    // MENSAJE DE EXITO AL CANCELAR
    public void muestraMensajeExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Cancelación Exitosa");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
        
        // CIERRA LA VENTANA
        cierra();
    }
    // MENSAJE DE ERROR AL NO PODER CANCELAR 
    public void muestraMensajeError(String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error al cancelar");
        alerta.setHeaderText("No se pudo completar la acción");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    // CIERRA LA VENTANA DE CANCELACION
    public void cierra() {
        if (volverBtn != null && volverBtn.getScene() != null) {
            Stage stage = (Stage) volverBtn.getScene().getWindow();
            stage.close();
        }
    }
}