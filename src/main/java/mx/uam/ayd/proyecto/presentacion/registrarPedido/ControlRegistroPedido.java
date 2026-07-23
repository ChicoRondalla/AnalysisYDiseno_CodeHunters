package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.presentacion.armadoOrden.VistaArmadoOrden;

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

    // Inyectamos la nueva ventana de Armado de Orden
    @Autowired
    private VistaArmadoOrden vistaArmadoOrden;

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

        boolean datosValidos = servicioPedido.validarDatosDomicilio(nombre, telefono, direccion);

        if (datosValidos) {
            // Atrapamos el pedido recién creado
            Pedido nuevoPedido = servicioPedido.crearPedidoDomicilio(nombre, telefono, direccion);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Registrado", "El pedido a domicilio se guardó correctamente en el sistema.");
            limpiarCampos();
            mostrarPantalla(paneSeleccion);
            
            // ¡Abrimos la pantalla del menú y le pasamos el ticket!
            vistaArmadoOrden.muestra(nuevoPedido);
            
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", 
                "Verifica que ningún campo esté vacío y que el teléfono tenga exactamente 10 dígitos.");
        }
    }

    @FXML
    void onContinuarRecogerAction(ActionEvent event) {
        String nombre = txtNombreRecoger.getText();
        String telefono = txtTelefonoRecoger.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", "El nombre del cliente es obligatorio.");
        } else {
            // Atrapamos el pedido recién creado
            Pedido nuevoPedido = servicioPedido.crearPedidoRecoger(nombre, telefono);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Registrado", "El pedido para recoger se guardó correctamente en el sistema.");
            limpiarCampos();
            mostrarPantalla(paneSeleccion);
            
            // ¡Abrimos la pantalla del menú y le pasamos el ticket!
            vistaArmadoOrden.muestra(nuevoPedido);
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

    // --- MÉTODOS PARA CONSUMO LOCAL (Mesas) ---

    /**
     * Método auxiliar para registrar la mesa seleccionada, 
     * mostrar la alerta y regresar al menú.
     */
    private void registrarPedidoMesa(int numeroMesa) {
        // Atrapamos el pedido recién creado (AQUÍ ESTABA EL DETALLE)
        Pedido nuevoPedido = servicioPedido.crearPedidoLocal(numeroMesa);
        
        mostrarAlerta(Alert.AlertType.INFORMATION, "Mesa registrada", 
            "La Mesa " + numeroMesa + " se ha seleccionado correctamente.");
            
        mostrarPantalla(paneSeleccion);

        // ¡Abrimos la pantalla del menú y le pasamos el ticket!
        vistaArmadoOrden.muestra(nuevoPedido);
    }

    @FXML
    void onMesa1Action(ActionEvent event) {
        registrarPedidoMesa(1);
    }

    @FXML
    void onMesa2Action(ActionEvent event) {
        registrarPedidoMesa(2);
    }

    @FXML
    void onMesa3Action(ActionEvent event) {
        registrarPedidoMesa(3);
    }

    @FXML
    void onMesa4Action(ActionEvent event) {
        registrarPedidoMesa(4);
    }

    @FXML
    void onMesa5Action(ActionEvent event) {
        registrarPedidoMesa(5);
    }
}