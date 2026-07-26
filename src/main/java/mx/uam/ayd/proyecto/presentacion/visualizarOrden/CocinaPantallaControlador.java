package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioOrden;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;

@Component
public class CocinaPantallaControlador {

    private static final Logger LOGGER = Logger.getLogger(CocinaPantallaControlador.class.getName());
    private static final String TXT_ORDEN_PREFIX = "Orden #";

    @FXML
    private Label lblEstacion;

    @FXML
    private TilePane containerPendientes;

    @FXML
    private Label txtOrdenInput;

    private String estacionActual = "ROLLOS";

    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    private ServicioOrden servicioOrden;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    @Lazy
    private ControlVisualizarOrden controlVisualizarOrden;

    @FXML
    public void initialize() {
        if (txtOrdenInput != null) {
            txtOrdenInput.setText(TXT_ORDEN_PREFIX);
        }
        actualizarVistaEstacion();
    }

    // --- ACCIONES DE NAVEGACIÓN Y ESTACIÓN ---

    @FXML
    private void handleCambiarEstacionRollos() {
        this.estacionActual = "ROLLOS";
        actualizarVistaEstacion();
        if (controlVisualizarOrden != null) {
            controlVisualizarOrden.cargarPedidosPendientes();
        }
    }

    @FXML
    private void handleCambiarEstacionPlancha() {
        this.estacionActual = "PLANCHA";
        actualizarVistaEstacion();
        if (controlVisualizarOrden != null) {
            controlVisualizarOrden.cargarPedidosPendientes();
        }
    }

    /**
     * Muestra la ventana modal con las órdenes completadas.
     */
    @FXML
    private void handleMostrarCompletados() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Órdenes Completadas - Cocina");

        VBox rootLayout = new VBox(15);
        rootLayout.setPadding(new Insets(20));
        rootLayout.setStyle("-fx-background-color: #1E1E1E;");

        Label titulo = new Label("ÓRDENES COMPLETADAS");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #00FF87;");

        TilePane containerCompletados = new TilePane();
        containerCompletados.setHgap(15);
        containerCompletados.setVgap(15);
        containerCompletados.setPrefColumns(3);

        List<Pedido> completados = servicioPedido.recuperaPedidosCompletados();

        if (completados == null || completados.isEmpty()) {
            Label lblVacio = new Label("No hay órdenes completadas recientemente.");
            lblVacio.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px;");
            rootLayout.getChildren().addAll(titulo, lblVacio);
        } else {
            for (Pedido p : completados) {
                VBox card = new VBox(6);
                card.setPrefWidth(200);
                card.setStyle("-fx-background-color: #2D2D2D; -fx-background-radius: 8; -fx-padding: 10px; -fx-border-color: #00FF87; -fx-border-radius: 8;");

                Label lblNum = new Label("Orden #" + p.getNumeroOrden());
                lblNum.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

                StringBuilder sb = new StringBuilder();
                List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(p.getIdPedido());
                if (detalles != null) {
                    for (DetallesPedido d : detalles) {
                        if (d.getPlatillo() != null) {
                            sb.append("• ").append(d.getCantidad()).append("x ").append(d.getPlatillo().getNombre()).append("\n");
                        }
                    }
                }

                Label lblItems = new Label(sb.toString().trim());
                lblItems.setWrapText(true);
                lblItems.setStyle("-fx-text-fill: #DDDDDD; -fx-font-size: 12px;");

                card.getChildren().addAll(lblNum, lblItems);
                containerCompletados.getChildren().add(card);
            }

            ScrollPane scrollPane = new ScrollPane(containerCompletados);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: #1E1E1E; -fx-background-color: transparent;");

            rootLayout.getChildren().addAll(titulo, scrollPane);
        }

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #E13131; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> modalStage.close());

        rootLayout.getChildren().add(btnCerrar);

        Scene scene = new Scene(rootLayout, 680, 480);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void actualizarVistaEstacion() {
        if (lblEstacion != null) {
            lblEstacion.setText("ESTACIÓN: " + estacionActual);
        }
    }

    // --- ACCIONES DEL TECLADO NUMÉRICO ---

    @FXML
    private void handleKeypadNumber(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String valorActual = txtOrdenInput.getText();

        if (TXT_ORDEN_PREFIX.equals(valorActual)) {
            txtOrdenInput.setText(btn.getText());
        } else {
            txtOrdenInput.setText(valorActual + btn.getText());
        }
    }

    @FXML
    private void handleKeypadClear() {
        if (txtOrdenInput != null) {
            txtOrdenInput.setText(TXT_ORDEN_PREFIX);
        }
    }

    @FXML
    private void handleKeypadSubmit() {
        String texto = txtOrdenInput.getText();
        if (!TXT_ORDEN_PREFIX.equals(texto) && !texto.isEmpty()) {
            try {
                int numeroOrden = Integer.parseInt(texto);
                boolean encontrado = servicioPedido.finalizarOrdenPorNumero(numeroOrden);
                
                if (encontrado) {
                    if (controlVisualizarOrden != null) {
                        controlVisualizarOrden.cargarPedidosPendientes();
                    }
                } else {
                    LOGGER.warning(() -> "No se encontró ningún pedido pendiente con el número de orden: " + numeroOrden);
                }
                
                handleKeypadClear();
            } catch (NumberFormatException e) {
                LOGGER.warning(() -> "Número de orden inválido en el pad: " + texto);
            }
        }
    }

    // --- MÉTODOS PARA RENDERIZAR Y FINALIZAR PEDIDOS ---

    public void finalizarOrden(Long idPedido) {
        try {
            Pedido pedido = servicioPedido.recuperaPedido(idPedido);
            if (pedido != null) {
                pedido.setEstado("Completado");
                
                // Persistencia directa mediante PedidoRepository
                pedidoRepository.save(pedido);
                
                if (controlVisualizarOrden != null) {
                    controlVisualizarOrden.cargarPedidosPendientes();
                }
            }
        } catch (Exception e) {
            LOGGER.severe(() -> "Error al finalizar la orden: " + e.getMessage());
        }
    }

    public void mostrarPedidosPendientes(List<Pedido> pedidos) {
        if (containerPendientes == null) return;
        containerPendientes.getChildren().clear();

        for (Pedido pedido : pedidos) {
            StringBuilder detallesTexto = new StringBuilder();

            List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedido.getIdPedido());

            if (detalles != null) {
                for (DetallesPedido detalle : detalles) {
                    Platillo platillo = detalle.getPlatillo();
                    
                    if (platillo != null && platillo.getTipoArea() != null && 
                        estacionActual.equalsIgnoreCase(platillo.getTipoArea())) {
                        
                        detallesTexto.append(detalle.getCantidad())
                                     .append("x ")
                                     .append(platillo.getNombre());
                        
                        if (detalle.getNotas() != null && !detalle.getNotas().trim().isEmpty()) {
                            detallesTexto.append(" (").append(detalle.getNotas()).append(")");
                        }
                        
                        detallesTexto.append("\n");
                    }
                }
            }

            if (!detallesTexto.toString().trim().isEmpty()) {
                crearTarjetaOrden(pedido.getIdPedido(), String.valueOf(pedido.getNumeroOrden()), detallesTexto.toString().trim());
            }
        }
    }

    private void crearTarjetaOrden(Long idPedido, String numeroOrden, String detallesPlatillo) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #2D1F21; -fx-background-radius: 8; -fx-border-color: #7B7374; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12px;");

        Label lblNum = new Label("#" + numeroOrden);
        lblNum.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Label lblDetalles = new Label(detallesPlatillo);
        lblDetalles.setWrapText(true);
        lblDetalles.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

        Button btnFinalizar = new Button("FINALIZAR");
        btnFinalizar.setStyle("-fx-background-color: #E13131; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnFinalizar.setOnAction(e -> finalizarOrden(idPedido));

        card.getChildren().addAll(lblNum, lblDetalles, btnFinalizar);
        containerPendientes.getChildren().add(card);
    }
}