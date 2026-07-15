package mx.uam.ayd.proyecto.presentacion.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioUsuario;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;

/**
 * Controlador de la vista de Login de Usuarios
 */
@Component // Fundamental para que Spring maneje este controlador
public class ControlLogin {

    @FXML
    private TextField txtIdUsuario;

    // Dependencias inyectadas por Spring Boot
    private final ServicioUsuario servicioUsuario;
    private final ControlPrincipal controlPrincipal;

    @Autowired
    public ControlLogin(ServicioUsuario servicioUsuario, ControlPrincipal controlPrincipal) {
        this.servicioUsuario = servicioUsuario;
        this.controlPrincipal = controlPrincipal;
    }

    @FXML
    void onIngresarAction(ActionEvent event) {
        String idIngresado = txtIdUsuario.getText();

        if (idIngresado == null || idIngresado.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío", "Por favor, ingresa tu ID de usuario.");
            return;
        }

        // Delegamos la lógica a la capa de negocio en lugar de llamar al Repositorio directamente
        Usuario usuario = servicioUsuario.validaUsuario(idIngresado.trim());

        if (usuario != null) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Bienvenido!", 
                    "Hola " + usuario.nombre + ", has ingresado con el rol: " + usuario.getRol());
            
            // ¡Aquí hacemos la conexión real! Cerramos (o ignoramos) la ventana actual y abrimos la principal
            controlPrincipal.inicia();
            
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Acceso", "El ID de usuario no existe.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}