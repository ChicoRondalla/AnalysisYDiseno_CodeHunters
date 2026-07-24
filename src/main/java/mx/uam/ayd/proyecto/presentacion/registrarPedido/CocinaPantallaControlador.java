package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import java.util.List;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private final ControlRegistroPedido controlRegistroPedido;

    @Autowired
    public CocinaPantallaControlador(ControlRegistroPedido controlRegistroPedido) {
        this.controlRegistroPedido = controlRegistroPedido;
    }

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
    }

    @FXML
    private void handleCambiarEstacionPlancha() {
        this.estacionActual = "PLANCHA";
        actualizarVistaEstacion();
    }

    @FXML
    private void handleMostrarCompletados() {
        LOGGER.info("Mostrando ventana / filtro de pedidos completados.");
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
                Long idOrden = Long.parseLong(texto);
                finalizarOrden(idOrden);
                handleKeypadClear();
            } catch (NumberFormatException e) {
                LOGGER.warning(() -> "Número de orden inválido: " + texto);
            }
        }
    }

    // --- MÉTODOS PARA RENDERIZAR Y FINALIZAR PEDIDOS ---

    public void finalizarOrden(Long idOrden) {
        LOGGER.info(() -> "Cambiando estado de la orden #" + idOrden + " a COMPLETADO.");
        
        if (controlRegistroPedido != null) {
            actualizarVistaEstacion();
        }
    }

    public void mostrarPedidosPendientes(List<Pedido> pedidos) {
        containerPendientes.getChildren().clear();

        for (Pedido pedido : pedidos) {
            StringBuilder detallesTexto = new StringBuilder();

            for (DetallesPedido detalle : pedido.getDetallesPedido()) {
                Platillo platillo = detalle.getPlatillo();
                
                if (platillo != null && estacionActual.equalsIgnoreCase(platillo.getTipoArea())) {
                    detallesTexto.append(detalle.getCantidad())
                                 .append("x ")
                                 .append(platillo.getNombre())
                                 .append("\n");
                }
            }

            if (!detallesTexto.isEmpty()) {
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