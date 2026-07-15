package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Vista para la Historia de Usuario HU-01: Registrar Pedido
 */
@Component
public class VistaRegistroPedido {

    private Stage stage;
    private ControlRegistroPedido control;
    private boolean initialized = false;

    public VistaRegistroPedido() {
        // El constructor se deja vacío por requerimiento de JavaFX con Spring
    }

    /**
     * Inicializa los componentes de la interfaz en el hilo de JavaFX
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Registrar Pedido");

            // Cargamos el archivo FXML (el cual diseñarás en Scene Builder)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-registro-pedido.fxml"));
            
            // Le decimos al FXML que su cerebro será tu controlador
            loader.setController(control);
            
            // Definimos el tamaño de la ventana según tus mockups
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra la ventana en pantalla
     * 
     * @param control El controlador encargado de la lógica
     */
    public void muestra(ControlRegistroPedido control) {
        this.control = control;
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(control));
            return;
        }

        initializeUI();
        stage.show();
    }
}
