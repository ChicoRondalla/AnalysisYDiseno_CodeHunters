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

/**
 * VistaCocinaPantalla: La encargada de construir y desplegar la ventana física.
 * ¿Qué papel juega? Mientras el controlador gestiona la lógica de la pantalla y el control de flujo 
 * coordina las órdenes, esta clase se encarga de abrir la "caja" (el Stage de JavaFX), buscar 
 * y leer el archivo de diseño visual (.fxml) y conectar a Spring para que los controladores nazcan con todas sus dependencias listas.
 */
@Component
public class VistaCocinaPantalla {

    private static final Logger LOGGER = Logger.getLogger(VistaCocinaPantalla.class.getName());

    private Stage stage; // La ventana principal de la cocina (el contenedor de alto nivel de JavaFX).
    private boolean initialized = false; // Bandera para saber si la ventana ya fue armada y evitar recrearla innecesariamente.
    
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
        // Si la ventana ya fue inicializada previamente, no hacemos nada para optimizar recursos y mantener el estado.
        if (initialized && stage != null) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Comanda de Cocina");

            // Intento 1: Buscar el diseño FXML usando el ClassLoader desde la carpeta raíz de resources.
            URL fxmlUrl = getClass().getClassLoader().getResource("fxml/CocinaPantallaVista.fxml");
            
            // Intento 2: Si el primero falla, buscamos el archivo de forma relativa justo al lado del paquete de este controlador.
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("CocinaPantallaVista.fxml");
            }

            // Si de plano no se encuentra el archivo en ninguna ruta, lanzamos un error crítico para avisar que falta el diseño visual.
            if (fxmlUrl == null) {
                throw new IOException("No se pudo encontrar el archivo CocinaPantallaVista.fxml en ninguna ruta conocida.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            
            // MAGIA DE SPRING + JAVAFX: Le decimos al cargador de FXML que use el contenedor de Spring 
            // para crear los controladores, permitiendo que funcionen las anotaciones @Autowired dentro de ellos.
            loader.setControllerFactory(context::getBean);

            // Cargamos el diseño en una escena con un tamaño estándar de 1080x720 píxeles para pantallas de cocina.
            Scene scene = new Scene(loader.load(), 1080, 720);
            stage.setScene(scene);

            initialized = true; // Marcamos que la interfaz ya quedó lista.
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el FXML de pantalla de cocina", e);
            e.printStackTrace();
        }
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