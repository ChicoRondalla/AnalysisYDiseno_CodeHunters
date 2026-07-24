package mx.uam.ayd.proyecto.presentacion.armadoOrden;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
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
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.VentanaEnviarOrdenCocina;

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
    private VentanaEnviarOrdenCocina ventanaCocina;

    private Pedido pedidoActual;

    public void inicia(Pedido pedido) {
        this.pedidoActual = pedido;
        lblMesaTicket.setText("TICKET #" + pedidoActual.getIdPedido());
        lblSubtotal.setText("$0.00");
        
        flowPanePlatillos.getChildren().clear();
        listResumenOrden.getItems().clear();
        
        // --- NUEVO: Detector de escritura en tiempo real ---
        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarPlatillo(newValue);
        });
        
        cargarPlatillosMenu(null);
    }
    
    // Modificamos el cargador para que acepte una categoría o cargue todo si es null
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
        tarjeta.setPadding(new Insets(10, 15, 15, 15)); // Arriba, Derecha, Abajo, Izquierda
        tarjeta.setCursor(Cursor.HAND);
        
        String estiloNormal = "-fx-background-color: #2b1c1c; -fx-background-radius: 12; -fx-border-color: #4a3333; -fx-border-radius: 12; -fx-border-width: 1;";
        String estiloHover = "-fx-background-color: #382424; -fx-background-radius: 12; -fx-border-color: #d62828; -fx-border-radius: 12; -fx-border-width: 1;";
        
        tarjeta.setStyle(estiloNormal);
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle(estiloHover));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle(estiloNormal));

        tarjeta.setOnMouseClicked(e -> {
            agregarPlatillo(String.valueOf(platillo.getIdPlatillo())); 
        });

        // 1. PRECIO (Esquina superior derecha)
        Label lblPrecio = new Label("$" + platillo.getPrecio());
        lblPrecio.setTextFill(Color.web("#e8b1b1"));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 13));
        
        HBox filaSuperior = new HBox(lblPrecio);
        filaSuperior.setAlignment(Pos.TOP_RIGHT); // Empuja el precio a la esquina
        
        // 2. ESPACIADOR (Empuja el nombre hacia abajo)
        Region espaciador = new Region();
        VBox.setVgrow(espaciador, Priority.ALWAYS);

        // 3. NOMBRE DEL PLATILLO (Lado izquierdo)
        Label lblNombre = new Label(platillo.getNombre());
        lblNombre.setTextFill(Color.WHITE);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true); // Permite múltiples líneas
        
        // EL TRUCO PARA ELIMINAR LOS 3 PUNTOS:
        // Le dice a JavaFX que expanda la altura del Label en lugar de truncar el texto
        lblNombre.setMinHeight(Region.USE_PREF_SIZE); 
        
        HBox filaInferior = new HBox(lblNombre);
        filaInferior.setAlignment(Pos.BOTTOM_LEFT); // Ancla el nombre a la izquierda

        // Armamos la tarjeta en orden: Precio arriba, espacio al centro, Nombre abajo
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
        txtNota.setText(notaGuardada); // <-- AHORA SÍ: Mostramos la nota guardada
        txtNota.setOnAction(e -> agregarNota(idDetalleStr, txtNota.getText()));

        Region espacio2 = new Region();
        HBox.setHgrow(espacio2, Priority.ALWAYS);

        HBox filaInferior = new HBox(5, btnMenos, lblCantidad, btnMas, espacio2, txtNota);
        filaInferior.setAlignment(Pos.CENTER_LEFT);

        celda.getChildren().addAll(filaSuperior, filaInferior);

        return celda;
    }

    /**
     * Método clave: Borra la lista visual y la vuelve a pintar leyendo H2.
     */
    private void actualizarVistaCarrito() {
        listResumenOrden.getItems().clear();
        List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedidoActual.getIdPedido());
        
        for(DetallesPedido detalle : detalles) {
             // Inyectamos la nota guardada si es que existe
             String notaGuardada = detalle.getNotas() == null ? "" : detalle.getNotas();
             
             VBox celda = crearCeldaDetalleCarrito(
                String.valueOf(detalle.getIdDetallePedido()), 
                detalle.getPlatillo().getNombre(), 
                detalle.getCantidad(), 
                detalle.getPlatillo().getPrecio(),
                notaGuardada // <-- Ahora pasamos la nota al creador de celdas
             );
             listResumenOrden.getItems().add(celda);
        }
        
        // Actualizamos el Label del Subtotal en pantalla
        double total = servicioOrden.calcularSubtotalTotal(pedidoActual.getIdPedido());
        lblSubtotal.setText("$" + total);
    }

    public void agregarPlatillo(String idPlatilloStr) {
        long idPlatillo = Long.parseLong(idPlatilloStr);
        servicioOrden.procesarNuevoPlatillo(idPlatillo, pedidoActual.getIdPedido());
        actualizarVistaCarrito(); // Refresca la pantalla
    }

    public void modificarCantidad(String idDetalleStr, int operacion) {
        long idDetalle = Long.parseLong(idDetalleStr);
        servicioOrden.procesarCambioCantidad(idDetalle, operacion);
        actualizarVistaCarrito(); // Refresca la pantalla (suma, resta o elimina visualmente)
    }

    public void agregarNota(String idDetalleStr, String nota) {
        long idDetalle = Long.parseLong(idDetalleStr);
        servicioOrden.agregarNota(idDetalle, nota);
    }

    @FXML
    public void mostrarTodoElMenu() {
        // Al pasar 'null', nuestro método ya sabe que debe cargar todo el catálogo
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

    /**
     * Filtra los platillos en pantalla dependiendo de lo que el usuario escriba.
     * Si escribe números busca por precio, si escribe letras busca por nombre.
     */
    private void buscarPlatillo(String textoBuscado) {
        flowPanePlatillos.getChildren().clear();
        List<Platillo> resultados;

        // Si la barra está vacía, mostramos todo el catálogo
        if (textoBuscado == null || textoBuscado.trim().isEmpty()) {
            resultados = (List<Platillo>) platilloRepository.findAll();
        } else {
            try {
                // Intentamos convertir el texto a número. Si funciona, es que busca por precio.
                int precioBuscado = Integer.parseInt(textoBuscado.trim());
                resultados = platilloRepository.findByPrecio(precioBuscado);
            } catch (NumberFormatException e) {
                // Si la conversión falla (da error), significa que escribió letras. Buscamos por nombre.
                resultados = platilloRepository.findByNombreContainingIgnoreCase(textoBuscado.trim());
            }
        }

        // Dibujamos las tarjetas que coincidieron con la búsqueda
        for (Platillo platillo : resultados) {
            VBox tarjeta = crearTarjetaPlatillo(platillo);
            flowPanePlatillos.getChildren().add(tarjeta);
        }
    }

    @FXML
    public void confirmarOrdenAction() {
        // Llamamos a la pantalla de la cocina (sin enviarle el pedido por ahora)
        ventanaCocina.muestra(); 
        
        // Cerramos la ventana del menú
        Stage stage = (Stage) btnConfirmarOrden.getScene().getWindow();
        stage.close();
    }

    
    @FXML
    public void initialize() {}
}