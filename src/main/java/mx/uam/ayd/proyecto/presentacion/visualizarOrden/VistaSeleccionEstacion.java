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

@Component
public class VistaSeleccionEstacion {

    private Stage stage; 
    private boolean initialized = false; 
    private final ApplicationContext context;

    @Autowired
    public VistaSeleccionEstacion(ApplicationContext context) {
        this.context = context;
    }

    private void initializeUI() {
        if (initialized && stage != null) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Selección de Estación"); // Título de la nueva ventana


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

    // NUEVO MÉTODO: Te servirá para ocultar esta pantalla cuando el cocinero elija su estación
    public void oculta() {
        if (stage != null && stage.isShowing()) {
            Platform.runLater(() -> stage.hide());
        }
    }
}