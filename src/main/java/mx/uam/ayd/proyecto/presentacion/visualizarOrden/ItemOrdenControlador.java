package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ItemOrdenControlador {

    @FXML private Label lblNumeroOrden; // El que dice "#002"
    @FXML private Label lblTemporizador; // El que dice "12:00:05"
    @FXML private FlowPane containerCheckboxes; // El FlowPane que contiene los CheckBox
    @FXML private Button btnFinalizar;

    private Long idPedido;
    private List<DetallesPedido> detalles;
    private CocinaPantallaControlador controladorPrincipal;
    private Timeline cardTimer;

    // Método principal para inyectar datos desde el controlador principal
    public void setDatos(Long idPedido, String numeroOrden, List<DetallesPedido> detalles, CocinaPantallaControlador controladorPrincipal) {
        this.idPedido = idPedido;
        this.detalles = detalles;
        this.controladorPrincipal = controladorPrincipal;

        lblNumeroOrden.setText("#" + numeroOrden);
        containerCheckboxes.getChildren().clear(); // Limpiamos el FlowPane de ejemplo

        configurarTemporizador();
        crearCheckboxes();
        evaluarBotonFinalizar();
    }

    private void configurarTemporizador() {
        LocalDateTime horaCreacion = LocalDateTime.now(); // Idealmente: pedido.getHoraCreacion()

        cardTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long segundosTranscurridos = ChronoUnit.SECONDS.between(horaCreacion, LocalDateTime.now());
            long minutos = segundosTranscurridos / 60;
            long segundos = segundosTranscurridos % 60;

            lblTemporizador.setText(String.format("%02d:%02d", minutos, segundos));

            // Alerta visual en rojo si supera 15 min (HU)
            if (minutos >= 15) {
                lblTemporizador.setStyle("-fx-text-fill: #da7871;"); // Rojo
            }
        }));
        
        cardTimer.setCycleCount(Timeline.INDEFINITE);
        cardTimer.play();
    }

    private void crearCheckboxes() {
        for (DetallesPedido detalle : detalles) {
            StringBuilder textoItem = new StringBuilder();
            textoItem.append(detalle.getCantidad()).append("x ").append(detalle.getPlatillo().getNombre());
            
            // Lógica HU-10: Platillos Cancelados
            boolean estaCancelado = false; 
            // Si tienes un campo 'estado' en DetallesPedido: 
            // estaCancelado = "Cancelado".equalsIgnoreCase(detalle.getEstado());
            
            if (estaCancelado) {
                textoItem.append(" (Cancelado)");
            } else if (detalle.getNotas() != null && !detalle.getNotas().trim().isEmpty()) {
                textoItem.append(" (").append(detalle.getNotas()).append(")");
            }

            CheckBox checkBox = new CheckBox(textoItem.toString());
            checkBox.setStyle("-fx-text-fill: #948b8b; -fx-padding: 5;");
            checkBox.setSelected(detalle.isCompletado());

            if (estaCancelado) {
                checkBox.setDisable(true);
            } else {
                checkBox.setOnAction(e -> {
                    detalle.setCompletado(checkBox.isSelected());
                    evaluarBotonFinalizar();
                });
            }

            containerCheckboxes.getChildren().add(checkBox);
        }
    }

    private void evaluarBotonFinalizar() {
        boolean todosListos = detalles.stream().allMatch(d -> d.isCompletado() || /* Lógica Cancelado d.getEstado().equals("Cancelado")*/ false);
        btnFinalizar.setDisable(!todosListos);
    }

    @FXML
    private void handleFinalizarOrden() {
        if (cardTimer != null) cardTimer.stop();
        controladorPrincipal.finalizarOrden(idPedido, detalles); // Llama al método de guardado en el controlador principal
        btnFinalizar.setDisable(true);
    }
    
    // Método para detener el timer externamente si es necesario al limpiar
    public void stopTimer() {
        if (cardTimer != null) cardTimer.stop();
    }
}