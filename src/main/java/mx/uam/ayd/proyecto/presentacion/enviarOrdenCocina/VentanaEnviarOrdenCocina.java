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

    // Inyectamos el contexto de Spring para que pueda fabricar los controladores
    @Autowired
    private ApplicationContext applicationContext;

    private Stage stage;

    /**
     * Muestra la ventana en la pantalla.
     */
    public void muestra() {
        try {
            // Buscamos tu archivo visual en la carpeta resources
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Ventana-enviar-orden-cocina.fxml"));
            
            // Magia de Spring Boot: le decimos que él controle las inyecciones (@Autowired) en el controlador
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
