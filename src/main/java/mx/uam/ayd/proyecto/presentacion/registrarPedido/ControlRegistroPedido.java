package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

/**
 * Controlador para la Historia de Usuario HU-01: Registrar Pedido (Selección y captura de datos).
 */
@Component
public class ControlRegistroPedido {

    // --- PANES (Las diferentes "pantallas" que se mostrarán/ocultarán) ---
    @FXML private Pane paneSeleccion;
    @FXML private Pane paneDomicilio;
    @FXML private Pane paneLocal;
    @FXML private Pane paneRecoger;

    // --- CAMPOS DE TEXTO (Formularios) ---
    @FXML private TextField txtNombreDomicilio;
    @FXML private TextField txtTelefonoDomicilio;
    @FXML private TextField txtDireccionDomicilio;
    
    @FXML private TextField txtNombreRecoger;
    @FXML private TextField txtTelefonoRecoger;

    // --- MÉTODOS DE NAVEGACIÓN (Botones principales) ---

    @FXML
    void onConsumoLocalAction(ActionEvent event) {
        // Ocultar selección, mostrar plano de mesas
    }

    @FXML
    void onParaRecogerAction(ActionEvent event) {
        // Ocultar selección, mostrar formulario de recoger
    }

    @FXML
    void onDomicilioAction(ActionEvent event) {
        // Ocultar selección, mostrar formulario de domicilio
    }

    @FXML
    void onRegresarAction(ActionEvent event) {
        // Ocultar cualquier formulario activo y volver a mostrar paneSeleccion
    }

    // --- MÉTODOS DE VALIDACIÓN Y CONTINUACIÓN ---

    @FXML
    void onContinuarDomicilioAction(ActionEvent event) {
        String nombre = txtNombreDomicilio.getText();
        String telefono = txtTelefonoDomicilio.getText();
        String direccion = txtDireccionDomicilio.getText();

        // TODO: Validar que no estén vacíos, que teléfono sea de 10 dígitos (RN-02)
        // TODO: Enviar datos a ServicioPedido
    }

    @FXML
    void onContinuarRecogerAction(ActionEvent event) {
        // Lógica similar para recoger (Recordando que teléfono es opcional según HU-01)
    }
}
