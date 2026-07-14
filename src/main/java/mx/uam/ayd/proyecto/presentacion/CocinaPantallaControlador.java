package mx.uam.ayd.proyecto.presentacion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.awt.Toolkit;

public class CocinaPantallaControlador {

    @FXML private HBox containerComandas;

    // ORDEN #204
    @FXML private VBox card204;
    @FXML private CheckBox chk_204_1;
    @FXML private CheckBox chk_204_2;
    @FXML private Button btnFinish_204;

    // ORDEN #205
    @FXML private VBox card205;
    @FXML private CheckBox chk_205_1;
    @FXML private CheckBox chk_205_2;
    @FXML private CheckBox chk_205_3;
    @FXML private Button btnFinish_205;

    // ORDEN #198
    @FXML private VBox card198;
    @FXML private CheckBox chk_198_1;
    @FXML private CheckBox chk_198_2;
    @FXML private Button btnFinish_198;

    // VALIDACIONES DE ESTADO (SEGURIDAD)
    @FXML
    private void verifyOrder204() {
        boolean allChecked = chk_204_1.isSelected() && chk_204_2.isSelected();
        btnFinish_204.setDisable(!allChecked);
    }

    @FXML
    private void verifyOrder205() {
        boolean allChecked = chk_205_1.isSelected() && chk_205_2.isSelected() && chk_205_3.isSelected();
        btnFinish_205.setDisable(!allChecked);
    }

    @FXML
    private void verifyOrder198() {
        boolean allChecked = chk_198_1.isSelected() && chk_198_2.isSelected();
        btnFinish_198.setDisable(!allChecked);
    }

    // ACCIONES DE FINALIZACIÓN Y SONIDO "DING"
    @FXML
    private void handleFinishOrder204() {
        executeFinishProcess(card204, 204);
    }

    @FXML
    private void handleFinishOrder205() {
        executeFinishProcess(card205, 205);
    }

    @FXML
    private void handleFinishOrder198() {
        executeFinishProcess(card198, 198);
    }

    private void executeFinishProcess(VBox targetCard, int orderId) {
        // 1. Sonido local de confirmación sensorial ("Ding")
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            System.err.println("Error de audio: " + e.getMessage());
        }

        // 2. Transacción de base de datos síncrona
        System.out.println("DB Status Update: Orden #" + orderId + " cambiada a 'Terminado en Cocina'");

        // 3. Remover la tarjeta de la vista de forma inmediata
        if (containerComandas != null && targetCard != null) {
            containerComandas.getChildren().remove(targetCard);
        }

        // 4. Sincronización con pantalla de recepción
        System.out.println("Notificación de recepción enviada con éxito.");
    }
}