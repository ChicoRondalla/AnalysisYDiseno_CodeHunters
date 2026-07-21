package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;

/**
 * Controlador para la Historia de Usuario HU-01: Registrar Pedido.
 */
@Component
public class ControlRegistroPedido {

    // --- PANES (Las diferentes "pantallas") ---
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

    // Inyectamos la capa de negocio para las validaciones
    @Autowired
    private ServicioPedido servicioPedido;

    // --- MÉTODO AUXILIAR PARA NAVEGACIÓN ---
    /**
     * Oculta todos los paneles y muestra únicamente el que se pasa por parámetro.
     */
    private void mostrarPantalla(Pane pantallaActiva) {
        paneSeleccion.setVisible(false);
        paneDomicilio.setVisible(false);
        paneLocal.setVisible(false);
        paneRecoger.setVisible(false);
        
        pantallaActiva.setVisible(true);
    }

    // --- MÉTODOS DE NAVEGACIÓN (Botones principales) ---

    @FXML
    void onConsumoLocalAction(ActionEvent event) {
        mostrarPantalla(paneLocal);
    }

    @FXML
    void onParaRecogerAction(ActionEvent event) {
        mostrarPantalla(paneRecoger);
    }

    @FXML
    void onDomicilioAction(ActionEvent event) {
        mostrarPantalla(paneDomicilio);
    }

    @FXML
    void onRegresarAction(ActionEvent event) {
        // Al regresar, limpiamos los campos por si el usuario había escrito algo
        limpiarCampos();
        mostrarPantalla(paneSeleccion);
    }

    // --- MÉTODOS DE VALIDACIÓN Y CONTINUACIÓN ---

    @FXML
    void onContinuarDomicilioAction(ActionEvent event) {
        String nombre = txtNombreDomicilio.getText();
        String telefono = txtTelefonoDomicilio.getText();
        String direccion = txtDireccionDomicilio.getText();

        // Validamos usando la capa de negocio (RN-02)
        boolean datosValidos = servicioPedido.validarDatosDomicilio(nombre, telefono, direccion);

        if (datosValidos) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos correctos. Redirigiendo al menú...");
            // TODO: Aquí llamaremos a la ventana del Menú en el futuro
        } else {
            // Cumpliendo el escenario 2 de la HU-01: Bloqueo por falta de datos
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", 
                "Verifica que ningún campo esté vacío y que el teléfono tenga exactamente 10 dígitos.");
        }
    }

    @FXML
    void onContinuarRecogerAction(ActionEvent event) {
        // Validación básica para recoger (Teléfono es opcional según HU-01)
        String nombre = txtNombreRecoger.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", "El nombre del cliente es obligatorio.");
        } else {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos correctos. Redirigiendo al menú...");
        }
    }

    // --- MÉTODOS UTILITARIOS ---

    private void limpiarCampos() {
        txtNombreDomicilio.clear();
        txtTelefonoDomicilio.clear();
        txtDireccionDomicilio.clear();
        txtNombreRecoger.clear();
        txtTelefonoRecoger.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}