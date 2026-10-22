package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
* Representa la vista fisica de la pantalla de seleccion de estacion 
* Se encarga de construir, mostrar y ocultar la ventana grafica  
* donde el cocinero elige su area operativa (Rollos, Postres, Bebidas o Plancha)
*/

@Component
public class VistaSeleccionEstacion {

    private Stage stage; 
    private boolean initialized = false; 
    private final ApplicationContext context;

    /**
    * Constructor principal de la vista de seleccion de estacion
    */

    @Autowired
    public VistaSeleccionEstacion(ApplicationContext context) {
        this.context = context;
    }

    /**
    * Construye la interfaz grafica (UI) cargando el archivo FXML de la seleccion de estacion
    */

    private void initializeUI() {
        if (initialized && stage != null) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Selección de Estación"); 


            URL fxmlUrl = getClass().getResource("/fxml/ventana-seleccion-estacion.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(context::getBean);

            Scene scene = new Scene(loader.load(), 1080, 720);
            stage.setScene(scene);

            initialized = true; 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Método para mostrar la ventana
    public void muestra(ControlVisualizarOrden control) {
        Runnable showTask = () -> {
            initializeUI(); 
            if (stage != null) {
                stage.show();        
                stage.toFront();     
                stage.requestFocus(); 
            }
        };

        if (Platform.isFxApplicationThread()) {
            showTask.run(); 
        } else {
            Platform.runLater(showTask); 
        }
    }

    //Metodo para ocultar la pantalla 

    public void oculta() {
        if (stage != null && stage.isShowing()) {
            Platform.runLater(() -> stage.hide());
        }
    }
}