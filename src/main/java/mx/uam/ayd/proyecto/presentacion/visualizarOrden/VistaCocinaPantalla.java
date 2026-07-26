package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.io.IOException;
import java.net.URL;
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

            // Intento 1: Buscar usando el ClassLoader desde la raíz de resources
            URL fxmlUrl = getClass().getClassLoader().getResource("fxml/CocinaPantallaVista.fxml");
            
            // Intento 2: Si no lo encuentra, buscar de forma relativa junto al paquete del controlador
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("CocinaPantallaVista.fxml");
            }

            if (fxmlUrl == null) {
                throw new IOException("No se pudo encontrar el archivo CocinaPantallaVista.fxml en ninguna ruta conocida.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(context::getBean);

            Scene scene = new Scene(loader.load(), 1080, 720);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el FXML de pantalla de cocina", e);
            e.printStackTrace();
        }
    }

    public void muestra(ControlVisualizarOrden control) {
        Runnable showTask = () -> {
            initializeUI();
            if (stage != null) {
                stage.show();
                stage.toFront();      // Trae la ventana al frente
                stage.requestFocus(); // Asegura que reciba el foco inmediatamente
            }
        };

        if (Platform.isFxApplicationThread()) {
            showTask.run();
        } else {
            Platform.runLater(showTask);
        }
    }
}