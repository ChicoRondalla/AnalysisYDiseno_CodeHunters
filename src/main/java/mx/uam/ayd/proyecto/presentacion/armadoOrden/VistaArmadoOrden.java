package mx.uam.ayd.proyecto.presentacion.armadoOrden;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

/**
 * Clase encargada de cargar y mostrar la ventana de Armado de Orden (HU-02)
 */
@Component
public class VistaArmadoOrden {

    @Autowired
    private ApplicationContext applicationContext;

    private Stage stage;

    /**
     * Muestra la ventana y le pasa el pedido actual al controlador.
     * 
     * @param pedido El pedido generado en la HU-01
     */
    public void muestra(Pedido pedido) {
        try {
            // CORRECCIÓN CLAVE: Usamos VistaArmadoOrden.class en lugar de getClass()
            URL url = VistaArmadoOrden.class.getResource("/fxml/vista-armado-orden.fxml");
            
            // Protección: Si el archivo no se encuentra, avisamos en lugar de colapsar
            if (url == null) {
                System.err.println("¡ERROR CRÍTICO! No se encontró el archivo en src/main/resources/fxml/Vista-Armado-Orden.fxml");
                System.err.println("Verifica que las mayúsculas, minúsculas y guiones coincidan exactamente.");
                return; 
            }

            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(applicationContext::getBean);
            
            Parent root = loader.load();

            ControlArmadoOrden controlador = loader.getController();

            // Configuración de la ventana
            stage = new Stage();
            stage.setTitle("Ryuho Sushi - Menú de Platillos");
            stage.setScene(new Scene(root));
            
            // Le pasamos el pedido al controlador antes de mostrar la ventana
            controlador.inicia(pedido);

            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz de Armado de Orden.");
            e.printStackTrace();
        }
    }
    
    public void cierra() {
        if (stage != null) {
            stage.close();
        }
    }
}