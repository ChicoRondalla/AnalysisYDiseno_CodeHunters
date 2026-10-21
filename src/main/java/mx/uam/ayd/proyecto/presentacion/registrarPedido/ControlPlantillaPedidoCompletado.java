package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Controlador de cada tarjeta destinada a mostrar un pedido completado para la pantalla principal en la HU-7 de la actualización de Ingeniería de Software
 * ControlPlantillaPedidoCompletado
 * @author JLCB
 */
public class ControlPlantillaPedidoCompletado {

    // Contenedor del pedido completado
    @FXML 
    private VBox pedidoCompletado;

    // Textos a modificar
    @FXML
    private Label numPedido;
    @FXML
    private Label tipoPedido;
    @FXML
    private Label mesa;
    @FXML 
    private Label cantProductos;

    // Secciones a aplicar hover
    @FXML 
    private HBox instruccionPasaParaVer;
    @FXML
    private VBox detallesPedidoCompletado;

    // Cosas dentro de los detalles del pedido
    @FXML
    private VBox cajaProductos;
    @FXML 
    private Button botonMarcarEntregado;

    // Control, conectado para poder llegar al servicio mediante él
    private ControlRegistroPedido control;

    // Métodos
    /**
     * Inicialización de la tarjeta
     */
    @FXML
    public void initialize() {
        // Inicializamos con  los detalles ocultos
        cambiarVisibilidadHover(false);

        // Se activa la disponbiilidad del hover
        pedidoCompletado.setOnMouseEntered(e -> cambiarVisibilidadHover(true));
        pedidoCompletado.setOnMouseExited(e -> cambiarVisibilidadHover(false));
    }

    /**
     * Se da el control que controlará a esta tarjeta
     * @param control Es específicamente el control que tendrá
     */
    public void setControl(ControlRegistroPedido control){
        this.control = control;
    }

    /**
     * Método encargado de decidir qué cosas ocultar dependiendo de si el mouse fue ingresado o retirado al área de la tarjeta
     * @param mostrar Es el que determina si el mouse está sobre la tarjeta o no, con True en caso de que esté sobre la tarjeta y False en caso de que haya salido
     */
    private void cambiarVisibilidadHover(boolean mostrar) {
        instruccionPasaParaVer.setManaged(!mostrar);
        instruccionPasaParaVer.setVisible(!mostrar);
        instruccionPasaParaVer.setDisable(mostrar);

        detallesPedidoCompletado.setManaged(mostrar);
        detallesPedidoCompletado.setVisible(mostrar);
        detallesPedidoCompletado.setDisable(!mostrar);
    }

    /**
     * Método encargado de analizar los datos del pedido y sus detalles para pintarlos en la tarjeta y en sus detalles
     * @param pedido Es el pedido que está dentro de la tarjeta
     */
    public void configurarDatos(Pedido pedido) {
        // Sacamos a todos los detalles del pedido
        List<DetallesPedido> detalles = pedido.getDetallesPedido();

        // Asignamos los datos del pedido
        numPedido.setText("Pedido #"+Integer.toString(pedido.getNumeroOrden()));
        if(pedido.getNumMesa() == 0) mesa.setText("Cliente: "+ pedido.getCliente().getNombre());
        else mesa.setText("Mesa "+pedido.getNumMesa());
        cantProductos.setText(Integer.toString(detalles.size())+" productos");

        // El tipo de pedido tiene un estilo específico dependiendo de cuál sea, aquí se decide cuál es el estilo que se le dará a esta tarjeta
        tipoPedido.setStyle(null);
        switch(pedido.getTipoOrden()){
            case "A Domicilio":
                tipoPedido.setText("A DOMICILIO");
                tipoPedido.setStyle(
                    "-fx-background-color: #31006B;"+
                    "-fx-text-fill: #7153C2;"+
                    "-fx-border-color: #7D20D4;"
                );
            break;
            case "Consumo Local":
            case "Local":
                tipoPedido.setText("CONSUMO LOCAL");
                tipoPedido.setStyle(
                    "-fx-background-color: #243F70;"+
                    "-fx-text-fill: #1FACFF;"+
                    "-fx-border-color: #1FACFF;"
                );
            break;
            case "Para Llevar":
                tipoPedido.setText("PARA RECOGER");
                tipoPedido.setStyle(
                    "-fx-background-color: #543400;"+
                    "-fx-text-fill: #C97D00;"+
                    "-fx-border-color: #C97D00;"
                );
            break;
            default:
                tipoPedido.setText("DESCONOCIDO");
                tipoPedido.setStyle(
                    "-fx-background-color: #4A4A4A;"+
                    "-fx-text-fill: #ABABAB;"+
                    "-fx-border-color: #ABABAB;"
                );
            break;
        }

        // Se crean todas las líneas que sean necesarias para ingresar a los platillos y sus cantidades según correspondan al pedido
        cajaProductos.getChildren().clear();
        // For para poder escribir a cada platillo que esté en los detalles
        for(DetallesPedido det : detalles){
            HBox fila = new HBox(5); // Creamos el HBox que contendrá al platillo
            fila.setAlignment(Pos.CENTER_LEFT); // Aseguramos que esté centrado verticalmente y hacía la izquierda

            Label cant = new Label(Integer.toString(det.getCantidad())+"x"); // Especificamos la cantidad de veces del platillo
            cant.setStyle("-fx-text-fill: #FF2F00; -fx-font-weight: bold;");
            cant.setMinWidth(Label.USE_PREF_SIZE); // Damos un mínimo para que no se quede tapado en caso de que tenga poco espacio

            Label nombre = new Label(det.getPlatillo().getNombre()); // Especificamos el nombre del platillo
            nombre.setStyle("-fx-text-fill: white;");

            Region espacio = new Region(); // Creamos un region para poder mandar a la derecha los siguientes elementos
            HBox.setHgrow(espacio, Priority.ALWAYS);

            Label nota = new Label(det.getNotas()); // Damos las notas que se hayan dado
            nota.setStyle("-fx-text-fill: white; -fx-opacity: 0.5");
            nota.setTextAlignment(TextAlignment.RIGHT);
            nota.setWrapText(true);
            nota.setMaxWidth(120);

            fila.getChildren().addAll(cant, nombre, espacio, nota); // Se agregan los datos al HBox
            cajaProductos.getChildren().add(fila); // Se agrega el HBox al contenedor

            // Se asigna el valor que guardará el botón para cuando sea presionado
            botonMarcarEntregado.setUserData(pedido);
        }
    }

    /**
     * Es la acción que se tomará en el caso de presionar al botón para Marcar como Entregado
     * @param event
     */
    @FXML
    private void onActionBotonMarcarEntregado(ActionEvent event){
        // Se recupera la información guardada en el botón
        Button botonPresionado = (Button) event.getSource();
        Object dato = botonPresionado.getUserData();
        if(dato == null) return;
        if(dato instanceof Pedido pedido){ // Evalúamos para asegurarnos de que sea de tipo Pedido
            control.marcarComoEntregado(pedido);
        }
        pedidoCompletado.setDisable(true); // Se deshabilita la tarjeta del pedido para evitar que se siga abriendo
    }
}