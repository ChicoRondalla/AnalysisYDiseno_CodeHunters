package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class VistaCocinaPantalla {

    private static final Logger LOGGER = Logger.getLogger(VistaCocinaPantalla.class.getName());

    private Stage stage;
    private boolean initialized = false;
    private final ApplicationContext context;

    @Autowired
    public VistaCocinaPantalla(ApplicationContext context) {
        this.context = context;
    }

    private void initializeUI() {
        if (initialized && stage != null) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Comanda de Cocina");

            // AQUÍ ESTABA EL ERROR: El archivo se llama 'CocinaPantallaVista.fxml'
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CocinaPantallaVista.fxml"));
            loader.setControllerFactory(context::getBean);

            Scene scene = new Scene(loader.load(), 1080, 720);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el FXML de pantalla de cocina", e);
        }
    }

    public void muestra(ControlVisualizarOrden control) {
        Runnable showTask = () -> {
            initializeUI();
            if (stage != null) {
                stage.show();
                stage.toFront();
            }
        };

        if (Platform.isFxApplicationThread()) {
            showTask.run();
        } else {
            Platform.runLater(showTask);
        }
    }
}