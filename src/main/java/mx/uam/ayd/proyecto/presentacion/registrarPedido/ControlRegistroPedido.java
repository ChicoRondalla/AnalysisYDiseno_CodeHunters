package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.presentacion.armadoOrden.VistaArmadoOrden;
import mx.uam.ayd.proyecto.presentacion.registrarMesa.ControlMesa;

/**
 * Controlador para la Historia de Usuario HU-01: Registrar Pedido.
 */
@Component
public class ControlRegistroPedido {

    // --- PANES (Solo las pantallas activas en el FXML limpio) ---
    @FXML private Pane paneSeleccion;
    @FXML private Pane paneDomicilio;
    @FXML private Pane paneRecoger;

    // --- CAMPOS DE TEXTO (Formularios) ---
    @FXML private TextField txtNombreDomicilio;
    @FXML private TextField txtTelefonoDomicilio;
    @FXML private TextField txtDireccionDomicilio;
    
    @FXML private TextField txtNombreRecoger;
    @FXML private TextField txtTelefonoRecoger;

    // Inyectamos la capa de negocio para los pedidos
    @Autowired
    private ServicioPedido servicioPedido;

    // Inyectamos el controlador de mesas para abrir el plano de sala nuevo
    @Autowired
    private ControlMesa controlMesa;

    // Inyectamos la nueva ventana de Armado de Orden
    @Autowired
    private VistaArmadoOrden vistaArmadoOrden;

    // --- MÉTODO AUXILIAR PARA NAVEGACIÓN ---
    private void mostrarPantalla(Pane pantallaActiva) {
        paneSeleccion.setVisible(false);
        paneDomicilio.setVisible(false);
        paneRecoger.setVisible(false);
        
        pantallaActiva.setVisible(true);
    }

    // --- MÉTODOS DE NAVEGACIÓN (Botones principales) ---

    @FXML
    public void onConsumoLocalAction(ActionEvent event) {
        // Abre la nueva ventana moderna de mesas y cierra esta
        controlMesa.inicia();
        cerrarVentana();
    }

    @FXML
    public void onParaRecogerAction(ActionEvent event) {
        mostrarPantalla(paneRecoger);
    }

    @FXML
    public void onDomicilioAction(ActionEvent event) {
        mostrarPantalla(paneDomicilio);
    }

    @FXML
    public void onRegresarAction(ActionEvent event) {
        limpiarCampos();
        mostrarPantalla(paneSeleccion);
    }

    // --- MÉTODOS DE VALIDACIÓN Y CONTINUACIÓN ---

    @FXML
    public void onContinuarDomicilioAction(ActionEvent event) {
        String nombre = txtNombreDomicilio.getText();
        String telefono = txtTelefonoDomicilio.getText();
        String direccion = txtDireccionDomicilio.getText();

        boolean datosValidos = servicioPedido.validarDatosDomicilio(nombre, telefono, direccion);

        if (datosValidos) {
            Pedido nuevoPedido = servicioPedido.crearPedidoDomicilio(nombre, telefono, direccion);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Registrado", "El pedido a domicilio se guardó correctamente en el sistema.");
            limpiarCampos();
            mostrarPantalla(paneSeleccion);
            
            vistaArmadoOrden.muestra(nuevoPedido);
            cerrarVentana();
            
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", 
                "Verifica que ningún campo esté vacío y que el teléfono tenga exactamente 10 dígitos.");
        }
    }

    @FXML
    public void onContinuarRecogerAction(ActionEvent event) {
        String nombre = txtNombreRecoger.getText();
        String telefono = txtTelefonoRecoger.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", "El nombre del cliente es obligatorio.");
        } else {
            Pedido nuevoPedido = servicioPedido.crearPedidoRecoger(nombre, telefono);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Registrado", "El pedido para recoger se guardó correctamente en el sistema.");
            limpiarCampos();
            mostrarPantalla(paneSeleccion);
            
            vistaArmadoOrden.muestra(nuevoPedido);
            cerrarVentana();
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

    private void cerrarVentana() {
        Stage stage = (Stage) paneSeleccion.getScene().getWindow();
        stage.close();
    }
}