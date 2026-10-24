package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controlador de presentacion para la tarjeta individual de cada orden en el tablero de cocina
 */

public class ItemOrdenControlador {

    @FXML private Label lblNumeroOrden; // Muestra el numero de orden
    @FXML private Label lblTemporizador; // Muestra el tiempo de preparacion
    @FXML private VBox containerCheckboxes; // El VBox que contiene los CheckBox
    @FXML private Button btnFinalizar; // Marca como terminada la orden en la estacion

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

    /**
     * Configura y arranca un cronometro reactivo que se actualiza cada segundo
     */

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

    /**
     * Genera dinamicamente los componentes para cada platillo dentro de los detalles del pedido
     */

    private void crearCheckboxes() {
        for (DetallesPedido detalle : detalles) {
            StringBuilder textoItem = new StringBuilder();
            textoItem.append(detalle.getCantidad()).append("x ").append(detalle.getPlatillo().getNombre());
            
            // Logica: Platillos Cancelados
            boolean estaCancelado = false; 
            
            if (estaCancelado) {
                textoItem.append(" (Cancelado)");
            } else if (detalle.getNotas() != null && !detalle.getNotas().trim().isEmpty()) {
                textoItem.append(" (").append(detalle.getNotas()).append(")");
            }

            CheckBox checkBox = new CheckBox(textoItem.toString());
            checkBox.setStyle("-fx-text-fill: #948b8b; -fx-padding: 5;");
            checkBox.setSelected(detalle.isCompletado());

            checkBox.setWrapText(true); // Permite el salto de línea
            checkBox.setPrefWidth(250); // Obliga a saltar de línea al llegar a los 250 pixeles

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

    /**
     * Evalua si todos los platillos de la orden han sido completados (o cancelados)
    */

    private void evaluarBotonFinalizar() {
        boolean todosListos = detalles.stream().allMatch(d -> d.isCompletado() || /* Logica Cancelado d.getEstado().equals("Cancelado")*/ false);
        btnFinalizar.setDisable(!todosListos);
    }

    /**
     * Maneja el evento de clic sobre el botón para finalizar la orden.
     */

    @FXML
    private void handleFinalizarOrden() {
        if (cardTimer != null) cardTimer.stop();
        controladorPrincipal.finalizarOrden(idPedido, detalles); // Llama al metodo de guardado en el controlador principal
        btnFinalizar.setDisable(true);
    }
    
    // Metodo para detener el timer externamente si es necesario al limpiar
    public void stopTimer() {
        if (cardTimer != null) cardTimer.stop();
    }
}