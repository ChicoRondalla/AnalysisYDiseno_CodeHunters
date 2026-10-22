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
import org.springframework.context.annotation.Scope;

/**
 * VistaCocinaPantalla: La encargada de construir y desplegar la ventana física.
 * ¿Qué papel juega? Mientras el controlador gestiona la lógica de la pantalla y el control de flujo 
 * coordina las órdenes, esta clase se encarga de abrir la "caja" (el Stage de JavaFX), buscar 
 * y leer el archivo de diseño visual (.fxml) y conectar a Spring para que los controladores nazcan con todas sus dependencias listas.
 */
@Component
@Scope("prototype")
public class VistaCocinaPantalla {

    private Stage stage; // La ventana principal de la cocina (el contenedor de alto nivel de JavaFX).
    private boolean initialized = false; // Bandera para saber si la ventana ya fue armada y evitar recrearla innecesariamente.
    private CocinaPantallaControlador controlador;

    // El contexto de Spring: nos sirve como "fábrica" para instanciar los controladores inyectando sus dependencias automáticamente.
    private final ApplicationContext context;

    /**
     * CONSTRUCTOR PRINCIPAL:
     * ¿Cuándo se llama? Cuando Spring detecta este componente y le inyecta el contexto general de la aplicación.
     */
    @Autowired
    public VistaCocinaPantalla(ApplicationContext context) {
        this.context = context;
    }

    /**
     * CONSTRUYE LA INTERFAZ GRÁFICA (UI):
     * ¿Cuándo se llama? Se ejecuta de manera interna la primera vez que se intenta mostrar la ventana.
     * Su trabajo es buscar el archivo FXML, cargarlo, aplicar el tamaño de la pantalla y enlazarlo con Spring.
     */
    
    private void initializeUI() {
        if (initialized && stage != null) { return; }
        try {
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Comanda de Cocina");
            URL fxmlUrl = getClass().getClassLoader().getResource("fxml/ventana-lista-ordenes.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(context::getBean);
            
            Scene scene = new Scene(loader.load(), 1080, 720);
            stage.setScene(scene);
            
            this.controlador = loader.getController(); 
            
            initialized = true; 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Inicializa la interfaz grasfica, despliega la ventana y retorna su controlador asociado
    */

    public CocinaPantallaControlador muestraYDevuelveControlador() {
        initializeUI(); 
        if (stage != null) {
            stage.show();        
            stage.toFront();     
        }
        return this.controlador;
    }
    
     //MUESTRA LA VENTANA EN LA PANTALLA:
    public void muestra(ControlVisualizarOrden control) {
        Runnable showTask = () -> {
            initializeUI(); // Arma la ventana la primera vez que se abre
            if (stage != null) {
                stage.show();        // Hace visible la ventana de la cocina en el monitor
                stage.toFront();     // Trae la ventana al frente de cualquier otra aplicación abierta
                stage.requestFocus(); // Asegura que reciba el foco del teclado/mouse de inmediato
            }
        };

        // Regla de oro de JavaFX: Modificar la interfaz gráfica siempre debe hacerse en el hilo principal de JavaFX.
        if (Platform.isFxApplicationThread()) {
            showTask.run(); // Si ya estamos en el hilo correcto, ejecutamos de inmediato.
        } else {
            Platform.runLater(showTask); // Si venimos de otro hilo (como un servicio o backend), lo mandamos a la fila segura de JavaFX.
        }
    }
}