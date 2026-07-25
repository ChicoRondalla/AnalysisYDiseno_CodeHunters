package mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class VentanaEnviarOrdenCocina {

    @Autowired
    private ApplicationContext applicationContext;

    private Stage stage;

    /**
     * Muestra la ventana reocupando la instancia administrada por Spring Boot.
     */
    public void muestra() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Ventana-enviar-orden-cocina.fxml"));
            
            // Le indicamos a JavaFX que obtenga los controladores desde el contenedor de Spring
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            
            Parent root = fxmlLoader.load();
            
            if (stage == null) {
                stage = new Stage();
            }
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Resumen de la Orden - Enviar a Cocina");
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la ventana de Enviar a Cocina:");
            e.printStackTrace();
        }
    }
}