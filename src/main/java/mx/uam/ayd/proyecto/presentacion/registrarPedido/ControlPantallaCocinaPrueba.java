package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Controlador de prueba temporal para ejecutar la pantalla de cocina de manera aislada.
 */
@Component
public class ControlPantallaCocinaPrueba {

    private final ApplicationContext context;

    @Autowired
    public ControlPantallaCocinaPrueba(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Levanta directamente la ventana JavaFX de la pantalla de cocina.
     */
    public void inicia() {
        try {
            // Carga el archivo FXML desde la carpeta resources/fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CocinaPantallaVista.fxml"));
            
            // Permite a Spring Boot inyectar las dependencias (@Autowired) en el controlador de la vista
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("PRUEBA - Pantalla de Cocina (KDS)");
            stage.setScene(new Scene(root, 1024, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}