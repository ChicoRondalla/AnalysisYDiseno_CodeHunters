package mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


@Component  // EVITA HACER EL NEW 
public class VentanaEnviarOrdenCocina {

    @Autowired // AUTOCABLEADO
    
    private ApplicationContext applicationContext;  // ES EL QUE TIENE EL CONTEXTO DE LA APP SERVICIOS Y CONTROLADORES
    
    private Stage stage;  // MARCO DE LA VENTANA

    /**
     * MUESTRA LA VENTANA 
     */
    public void muestra() {
        try {
            // TRADUCE EL FXML A CODIGO
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Ventana-enviar-orden-cocina.fxml"));
            
            // DA EL CONTROLADOR CON SUS SERVICIOS
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            
            Parent root = fxmlLoader.load(); // PROCESA EL ARCHIVO
            // AHORRA MEMORIA 
            if (stage == null) {
                stage = new Stage();
            }
            
            Scene scene = new Scene(root); // CONTENIDO VISUAL
            stage.setScene(scene);
            stage.setTitle("Resumen de la Orden - Enviar a Cocina");
            stage.show();
            
            // MENSAJE DE ERROR POR SI HAY ALGUN PROBLEMA CON EL FXML
        } catch (Exception e) {
            System.err.println("Error al cargar la ventana de Enviar a Cocina:");
            e.printStackTrace();
        }
    }
}