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
import mx.uam.ayd.proyecto.negocio.ServicioOrden;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;

@Controller
public class ControlArmadoOrden {

    @FXML private TextField txtBusqueda;
    @FXML private FlowPane flowPanePlatillos;
    @FXML private Label lblMesaTicket;
    @FXML private ListView<VBox> listResumenOrden; 
    @FXML private Label lblSubtotal;
    @FXML private Button btnConfirmarOrden;

    @Autowired
    private PlatilloRepository platilloRepository;

    @Autowired
    private ServicioOrden servicioOrden;

    @Autowired
    private ControlEnviarOrdenCocina controlEnviarOrdenCocina;

    private Pedido pedidoActual;

    public void inicia(Pedido pedido) {
        this.pedidoActual = pedido;
        lblMesaTicket.setText("TICKET #" + pedidoActual.getIdPedido());
        lblSubtotal.setText("$0.00");
        
        flowPanePlatillos.getChildren().clear();
        listResumenOrden.getItems().clear();
        
        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarPlatillo(newValue);
        });
        
        cargarPlatillosMenu(null);
    }
    
    private void cargarPlatillosMenu(String categoria) {
        flowPanePlatillos.getChildren().clear();
        List<Platillo> catalogo;
        
        if (categoria == null) {
            catalogo = (List<Platillo>) platilloRepository.findAll();
        } else {
            catalogo = platilloRepository.findByTipoArea(categoria);
        }
        
        for (Platillo platillo : catalogo) {
            VBox tarjeta = crearTarjetaPlatillo(platillo);
            flowPanePlatillos.getChildren().add(tarjeta);
        }
    }

    private VBox crearTarjetaPlatillo(Platillo platillo) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(150, 160);
        tarjeta.setPadding(new Insets(10, 15, 15, 15));
        tarjeta.setCursor(Cursor.HAND);
        
        String estiloNormal = "-fx-background-color: #2b1c1c; -fx-background-radius: 12; -fx-border-color: #4a3333; -fx-border-radius: 12; -fx-border-width: 1;";
        String estiloHover = "-fx-background-color: #382424; -fx-background-radius: 12; -fx-border-color: #d62828; -fx-border-radius: 12; -fx-border-width: 1;";
        
        tarjeta.setStyle(estiloNormal);
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle(estiloHover));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle(estiloNormal));

        tarjeta.setOnMouseClicked(e -> {
            agregarPlatillo(String.valueOf(platillo.getIdPlatillo())); 
        });

        Label lblPrecio = new Label("$" + platillo.getPrecio());
        lblPrecio.setTextFill(Color.web("#e8b1b1"));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 13));
        
        HBox filaSuperior = new HBox(lblPrecio);
        filaSuperior.setAlignment(Pos.TOP_RIGHT);
        
        Region espaciador = new Region();
        VBox.setVgrow(espaciador, Priority.ALWAYS);

        Label lblNombre = new Label(platillo.getNombre());
        lblNombre.setTextFill(Color.WHITE);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);
        lblNombre.setMinHeight(Region.USE_PREF_SIZE); 
        
        HBox filaInferior = new HBox(lblNombre);
        filaInferior.setAlignment(Pos.BOTTOM_LEFT);

        tarjeta.getChildren().addAll(filaSuperior, espaciador, filaInferior);

        return tarjeta;
    }

    private VBox crearCeldaDetalleCarrito(String idDetalleStr, String nombrePlatillo, int cantidad, double precioUnitario, String notaGuardada) {
        VBox celda = new VBox(5);
        celda.setPadding(new Insets(10));
        celda.setStyle("-fx-border-color: #4a3333; -fx-border-width: 0 0 1 0;");

        Label lblNombre = new Label(nombrePlatillo);
        lblNombre.setTextFill(Color.BLACK);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));

        Region espacio1 = new Region();
        HBox.setHgrow(espacio1, Priority.ALWAYS);

        Label lblSubtotalLocal = new Label("$" + (precioUnitario * cantidad));
        lblSubtotalLocal.setFont(Font.font("System", FontWeight.BOLD, 14));

        HBox filaSuperior = new HBox(lblNombre, espacio1, lblSubtotalLocal);
        filaSuperior.setAlignment(Pos.CENTER_LEFT);

        Button btnMenos = new Button("-");
        btnMenos.setPrefWidth(30);
        btnMenos.setOnAction(e -> modificarCantidad(idDetalleStr, -1));

        Label lblCantidad = new Label(String.valueOf(cantidad));
        lblCantidad.setPrefWidth(20);
        lblCantidad.setAlignment(Pos.CENTER);

        Button btnMas = new Button("+");
        btnMas.setPrefWidth(30);
        btnMas.setOnAction(e -> modificarCantidad(idDetalleStr, 1));

        TextField txtNota = new TextField();
        txtNota.setPromptText("Nota (ej. sin aderezo)");
        txtNota.setPrefWidth(120);
        txtNota.setText(notaGuardada);
        txtNota.setOnAction(e -> agregarNota(idDetalleStr, txtNota.getText()));

        Region espacio2 = new Region();
        HBox.setHgrow(espacio2, Priority.ALWAYS);

        HBox filaInferior = new HBox(5, btnMenos, lblCantidad, btnMas, espacio2, txtNota);
        filaInferior.setAlignment(Pos.CENTER_LEFT);

        celda.getChildren().addAll(filaSuperior, filaInferior);

        return celda;
    }

    private void actualizarVistaCarrito() {
        listResumenOrden.getItems().clear();
        List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedidoActual.getIdPedido());
        
        for(DetallesPedido detalle : detalles) {
             String notaGuardada = detalle.getNotas() == null ? "" : detalle.getNotas();
             
             VBox celda = crearCeldaDetalleCarrito(
                String.valueOf(detalle.getIdDetallePedido()), 
                detalle.getPlatillo().getNombre(), 
                detalle.getCantidad(), 
                detalle.getPlatillo().getPrecio(),
                notaGuardada
             );
             listResumenOrden.getItems().add(celda);
        }
        
        double total = servicioOrden.calcularSubtotalTotal(pedidoActual.getIdPedido());
        lblSubtotal.setText("$" + total);
    }

    public void agregarPlatillo(String idPlatilloStr) {
        long idPlatillo = Long.parseLong(idPlatilloStr);
        servicioOrden.procesarNuevoPlatillo(idPlatillo, pedidoActual.getIdPedido());
        actualizarVistaCarrito();
    }

    public void modificarCantidad(String idDetalleStr, int operacion) {
        long idDetalle = Long.parseLong(idDetalleStr);
        servicioOrden.procesarCambioCantidad(idDetalle, operacion);
        actualizarVistaCarrito();
    }

    public void agregarNota(String idDetalleStr, String nota) {
        long idDetalle = Long.parseLong(idDetalleStr);
        servicioOrden.agregarNota(idDetalle, nota);
    }

    @FXML
    public void mostrarTodoElMenu() {
        cargarPlatillosMenu(null); 
    }

    @FXML
    public void filtrarRollos() {
        cargarPlatillosMenu("Rollos");
    }

    @FXML
    public void filtrarPlancha() {
        cargarPlatillosMenu("Plancha");
    }

    @FXML
    public void filtrarBebidas() {
        cargarPlatillosMenu("Bebidas");
    }

    private void buscarPlatillo(String textoBuscado) {
        flowPanePlatillos.getChildren().clear();
        List<Platillo> resultados;

        if (textoBuscado == null || textoBuscado.trim().isEmpty()) {
            resultados = (List<Platillo>) platilloRepository.findAll();
        } else {
            try {
                int precioBuscado = Integer.parseInt(textoBuscado.trim());
                resultados = platilloRepository.findByPrecio(precioBuscado);
            } catch (NumberFormatException e) {
                resultados = platilloRepository.findByNombreContainingIgnoreCase(textoBuscado.trim());
            }
        }

        for (Platillo platillo : resultados) {
            VBox tarjeta = crearTarjetaPlatillo(platillo);
            flowPanePlatillos.getChildren().add(tarjeta);
        }
    }

    @FXML
    public void confirmarOrdenAction() {
        if (pedidoActual != null) {
            // Pasamos el objeto pedido completo al controlador de resumen
            controlEnviarOrdenCocina.inicia(pedidoActual);
        }
    }

    @FXML
    public void initialize() {}
}