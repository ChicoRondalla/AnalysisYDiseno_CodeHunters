package mx.uam.ayd.proyecto.presentacion.agregarEmpleado;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioEmpleado;
import mx.uam.ayd.proyecto.presentacion.tablaEmpleados.ControlTablaEmpleados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Controlador para la ventana modal de registro de un nuevo empleado.
 */
@Component
public class ControlAgregarEmpleado {

    @Autowired
    private ServicioEmpleado servicioEmpleado;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    private Label lblMensaje;

    private Stage stage;
    private ControlTablaEmpleados controlTabla;

    @FXML
    public void initialize() {
        if (comboRol != null) {
            comboRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Recepcionista",
                "Cocinero",
                "Repartidor"
            ));
            comboRol.getSelectionModel().select("Cocinero");
        }
    }

    /**
     * Muestra la ventana modal de registro.
     */
    public void muestra(ControlTablaEmpleados controlTabla) {
        this.controlTabla = controlTabla;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-registro-empleado.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            stage = new Stage();
            stage.setTitle("Registrar Empleado - Ryuho Sushi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            
            limpiarCampos();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void actionGuardar() {
        lblMensaje.setText("");
        String nombre = txtNombre.getText();
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();
        String rol = comboRol.getValue();

        try {
            // Nota: Si el metodo en ServicioEmpleado.java se llama agregaEmpleado o agregarEmpleado,
            // ajusta solo este nombre de metodo segun tu Tarea 2:
            servicioEmpleado.registrarEmpleado(nombre, usuario, contrasena, rol);
            
            if (controlTabla != null) {
                controlTabla.actualizarTabla();
            }
            stage.close();
        } catch (IllegalArgumentException e) {
            lblMensaje.setStyle("-fx-text-fill: #ef4444;");
            lblMensaje.setText(e.getMessage());
        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: #ef4444;");
            lblMensaje.setText("Error al guardar el empleado. Intente de nuevo.");
        }
    }

    @FXML
    public void actionCancelar() {
        if (stage != null) {
            stage.close();
        }
    }

    private void limpiarCampos() {
        if (txtNombre != null) txtNombre.clear();
        if (txtUsuario != null) txtUsuario.clear();
        if (txtContrasena != null) txtContrasena.clear();
        if (comboRol != null) comboRol.getSelectionModel().select("Cocinero");
        if (lblMensaje != null) lblMensaje.setText("");
    }
}