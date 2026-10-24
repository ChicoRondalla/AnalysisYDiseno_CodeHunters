package mx.uam.ayd.proyecto.presentacion.loginAdministrador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioAutenticacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Controlador de la ventana modal de login exclusivo para administradores.
 */
@Component
public class ControlLoginAdministrador {

    @Autowired
    private ServicioAutenticacion servicioAutenticacion;

    @Autowired
    private ApplicationContext springContext;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblMensaje;

    private Stage stage;
    private boolean exito;

    /**
     * Muestra el login y espera a que se cierre.
     * @return true si el administrador inició sesión correctamente.
     */
    public boolean muestra() {
        exito = false;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-login-administrador.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            stage = new Stage();
            stage.setTitle("Acceso de Administrador - Ryuho Sushi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exito;
    }

    @FXML
    public void actionIngresar() {
        lblMensaje.setText("");
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isEmpty()) {
            lblMensaje.setText("Ingresa tu usuario y contraseña.");
            return;
        }

        if (servicioAutenticacion.autenticarAdministrador(usuario.trim(), contrasena)) {
            exito = true;
            stage.close();
        } else {
            // Mensaje genérico: no revela si el usuario existe o si simplemente no es admin
            lblMensaje.setText("Credenciales incorrectas o sin permisos de administrador.");
            txtContrasena.clear();
        }
    }

    @FXML
    public void actionCancelar() {
        if (stage != null) {
            stage.close();
        }
    }
}