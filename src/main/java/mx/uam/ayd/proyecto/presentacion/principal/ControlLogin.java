package mx.uam.ayd.proyecto.presentacion.login;

import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import mx.uam.ayd.proyecto.datos.UsuarioRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.util.Optional;


//Controlador de la vista de Login de Usuarios (EMPLEADOS)
public class ControlLogin {

    @FXML
    private TextField txtIdUsuario;

    private final UsuarioRepository usuarioRepository;

    public ControlLogin() {
        // Inicialización directa del repositorio
        this.usuarioRepository = new UsuarioRepository();
    }

    @FXML
    void onIngresarAction(ActionEvent event) {
        String idIngresado = txtIdUsuario.getText();

        if (idIngresado == null || idIngresado.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío", "Por favor, ingresa tu ID de usuario.");
            return;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idIngresado.trim());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Bienvenido!", 
                    "Hola " + usuario.nombre + ", has ingresado con el rol: " + usuario.getRol());
            
            // Aquí iria la logica a VentanaPrincipal o CocinaPantalla, dependiendo el rol del usuario :b
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