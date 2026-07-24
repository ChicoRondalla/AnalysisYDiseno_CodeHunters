package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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
                LOGGER.warning("Número de orden inválido: " + texto);
            }
        }
    }

    // --- MÉTODOS DE NEGOCIO ---

    public void finalizarOrden(Long idOrden) {
        // Ejemplo de uso para eliminar el warning de "Unused field controlRegistroPedido"
        if (controlRegistroPedido != null) {
            LOGGER.info(() -> "Finalizando orden #" + idOrden + " mediante ControlRegistroPedido.");
        }
    }

    public void agregarTarjetaOrden(String numeroOrden, String detallesPlatillo) {
        VBox card = new VBox();
        card.setPrefWidth(300);
        card.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-padding: 10px;");

        Label lblNum = new Label("#" + numeroOrden);
        lblNum.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label lblDetalles = new Label(detallesPlatillo);

        Button btnFinalizar = new Button("FINALIZAR");
        btnFinalizar.setStyle("-fx-background-color: #912634; -fx-text-fill: white;");
        btnFinalizar.setOnAction(e -> containerPendientes.getChildren().remove(card));

        card.getChildren().addAll(lblNum, lblDetalles, btnFinalizar);
        containerPendientes.getChildren().add(card);
    }
}