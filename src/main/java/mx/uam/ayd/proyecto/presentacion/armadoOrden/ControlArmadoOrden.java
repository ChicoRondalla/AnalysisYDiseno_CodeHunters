package mx.uam.ayd.proyecto.presentacion.armadoOrden;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mx.uam.ayd.proyecto.datos.PlatilloRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;

/**
 * Controlador para la pantalla de Armado de Orden (HU-02)
 */
@Controller
public class ControlArmadoOrden {

    // --- COMPONENTES DE LA INTERFAZ (FXML) ---
    @FXML private TextField txtBusqueda;
    @FXML private FlowPane flowPanePlatillos;
    @FXML private Label lblMesaTicket;
    @FXML private ListView<String> listResumenOrden; 
    @FXML private Label lblSubtotal;
    @FXML private Button btnConfirmarOrden;

    // --- DEPENDENCIAS ---
    @Autowired
    private PlatilloRepository platilloRepository;

    private Pedido pedidoActual;

    /**
     * Método que arranca la funcionalidad de la pantalla.
     */
    public void inicia(Pedido pedido) {
        this.pedidoActual = pedido;
        lblMesaTicket.setText("TICKET #" + pedidoActual.getIdPedido());
        lblSubtotal.setText("$0.00");
        
        // 1. Limpiamos el panel por si tenía datos basura de SceneBuilder
        flowPanePlatillos.getChildren().clear();
        
        // 2. Cargamos los platillos desde la Base de Datos
        cargarPlatillosMenu();
    }
    
    /**
     * Consulta la base de datos y genera una tarjeta visual por cada platillo.
     */
    private void cargarPlatillosMenu() {
        // Obtenemos todos los platillos (puedes castear si tu repo devuelve Iterable)
        List<Platillo> catalogo = (List<Platillo>) platilloRepository.findAll();

        for (Platillo platillo : catalogo) {
            VBox tarjeta = crearTarjetaPlatillo(platillo);
            flowPanePlatillos.getChildren().add(tarjeta);
        }
    }

    /**
     * Dibuja y configura el estilo de una tarjeta individual para un Platillo.
     * Respeta el diseño oscuro de Ryuho Sushi.
     */
    private VBox crearTarjetaPlatillo(Platillo platillo) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(150, 160); // Tamaño estándar de la tarjeta
        tarjeta.setPadding(new Insets(15));
        tarjeta.setAlignment(Pos.BOTTOM_LEFT);
        tarjeta.setCursor(Cursor.HAND);
        
        // Estilos CSS directos en Java para el fondo y bordes
        String estiloNormal = "-fx-background-color: #2b1c1c; -fx-background-radius: 12; -fx-border-color: #4a3333; -fx-border-radius: 12; -fx-border-width: 1;";
        String estiloHover = "-fx-background-color: #382424; -fx-background-radius: 12; -fx-border-color: #d62828; -fx-border-radius: 12; -fx-border-width: 1;";
        
        tarjeta.setStyle(estiloNormal);

        // Efectos al pasar el mouse por encima (Hover)
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle(estiloHover));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle(estiloNormal));

        // Evento CLIC: ¿Qué pasa cuando seleccionas la tarjeta?
        tarjeta.setOnMouseClicked(e -> {
            System.out.println("¡Se hizo clic en el platillo: " + platillo.getNombre() + "!");
            // TODO: Aquí enlazaremos la lógica para agregar al carrito y sumar al subtotal
        });

        // --- COMPONENTES INTERNOS DE LA TARJETA ---

        // Nombre del platillo
        Label lblNombre = new Label(platillo.getNombre());
        lblNombre.setTextFill(Color.WHITE);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);

        // Espaciador para empujar el texto hacia abajo (simulando que arriba iría una imagen)
        Region espaciador = new Region();
        VBox.setVgrow(espaciador, Priority.ALWAYS);

        // Precio del platillo
        Label lblPrecio = new Label("$" + platillo.getPrecio());
        lblPrecio.setTextFill(Color.web("#e8b1b1"));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 13));

        // Acomodo horizontal inferior (Nombre a la izq, precio a la derecha)
        HBox panelTextos = new HBox(lblNombre);
        panelTextos.setAlignment(Pos.CENTER_LEFT);
        Region espacioMedio = new Region();
        HBox.setHgrow(espacioMedio, Priority.ALWAYS);
        
        HBox filaInferior = new HBox(lblNombre, espacioMedio, lblPrecio);
        filaInferior.setAlignment(Pos.BOTTOM_CENTER);

        tarjeta.getChildren().addAll(espaciador, filaInferior);

        return tarjeta;
    }

    @FXML
    public void initialize() {
        // Inicialización de componentes (si es necesario)
    }
}