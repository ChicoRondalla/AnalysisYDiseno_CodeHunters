package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

/**
 * Vista para la Historia de Usuario HU-01: Registrar Pedido
 */
@Component
public class VistaRegistroPedido {

    private Stage stage;
    private ControlRegistroPedido control;
    private boolean initialized = false;

    // Elementos del contenedor de tarjetas
    @FXML
    private VBox cajaDePedidos;
    // Label no hay pedidos
    @FXML
    private Label noPedidosCompletos;

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
            // Le decimos al FXML que sólo tendrá contacto con la Vista. La vista tendrá contacto con el controlador
            //loader.setController(this);
            
            // Definimos el tamaño de la ventana según tus mockups
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Debido a que Vista es llamada desde un control ajeno y no uno propio, se necesita hacer el llamado desde este inicializador para que al inicio también aparezca
        control.cargarPedidos(); // Manda a control a cargar también los pedidos completados
    }

    /**
     * Método que recibe la lista de pedidos dada por control y muestra a todos los pedidos que se encuentren con ayuda de ControlPlantillaPedidoCompletado para dar detalles a cada uno
     * @author JLCB
     * @param listaPedidos Es la lista de pedidos completados y, por lo tanto, pedidos que necesitamos mostrar
     */
    public void dibujarTarjetasPedidos(List<Pedido> listaPedidos){
        // Oculta el mensaje que dice que no hay pedidos
        noPedidosCompletos.setManaged(false);
        noPedidosCompletos.setVisible(false);
        noPedidosCompletos.setDisable(true);
        // Muestra el contenedor que contiene a los mensajes
        cajaDePedidos.setManaged(true);
        cajaDePedidos.setVisible(true);
        cajaDePedidos.setDisable(false);

        cajaDePedidos.getChildren().clear(); // "Prepara" la caja en la que estarán las tarjetas
        for(Pedido pedido : listaPedidos){
            try{
                // Carga a la plantilla de la tarjeta de los pedidos
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/plantilla-pedido-completado.fxml"));
                ControlPlantillaPedidoCompletado tarjeta = new ControlPlantillaPedidoCompletado(); // Se asigna el controlador hecho a la tarjeta
                tarjeta.setControl(control); // Se da que este controlador tiene el control sobre las tarjetas, para apoyar la comunicación con los servicios
                loader.setController(tarjeta);
                Node tarjetaVisual = loader.load(); // Se inicializa la tarjeta
                tarjeta.configurarDatos(pedido); // Mediante el controlador se llama al método para ingresar la información
                cajaDePedidos.getChildren().add(tarjetaVisual); // Se agrega la tarjeta al contenedor
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * Método dado en caso de que la lista de pedido completados esté vacía. Se encarga de mostrar el mensaje que informa que no hay pedidos completados y oculta al contenedor de los pedidos para evitar errores
     * @author JLCB
     */
    public void dibujarNingunaTarjetaPedido(){
        // Muestra el mensaje de ningún pedido completado
        noPedidosCompletos.setManaged(true);
        noPedidosCompletos.setVisible(true);
        noPedidosCompletos.setDisable(false);
        // Oculta el contenedor de pedidos completados
        cajaDePedidos.setManaged(false);
        cajaDePedidos.setVisible(false);
        cajaDePedidos.setDisable(true);
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

        // Funciones con el fin de que la regresar se queden en sí mismas sin necesidad de maximizarla uno mismo
        stage.setMaximized(true);
        stage.show();
        stage.toFront();

        control.cargarPedidos();
    }

    public void cierraVentana(){
        stage.close();
    }
}
