package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioOrden;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;

/**
 * CocinaPantallaControlador: El corazón visual de la cocina.
 * Este controlador maneja todo lo que el cocinero ve y hace en la pantalla:
 * cambiar entre estaciones (Rollos o Plancha), interactuar con los platillos mediante checkboxes,
 * usar un teclado numérico en pantalla para buscar/finalizar órdenes, y consultar el historial de completados.
 */
@Component
public class CocinaPantallaControlador {

    private static final Logger LOGGER = Logger.getLogger(CocinaPantallaControlador.class.getName());
    private static final String TXT_ORDEN_PREFIX = "Orden #";

    // --- COMPONENTES DE LA VISTA (Inyectados desde FXML) ---
    @FXML
    private Label lblEstacion; // Etiqueta que muestra en qué estación estamos trabajando actualmente ("ROLLOS" o "PLANCHA").

    @FXML
    private TilePane containerPendientes; // Contenedor en forma de cuadrícula donde se dibujan las tarjetas con las órdenes pendientes.

    @FXML
    private Label txtOrdenInput; // Pantalla pequeña o texto donde se van acumulando los números que el cocinero tipea en el pad numérico.

    // Estado interno para saber qué área de la cocina está activa por defecto al arrancar.
    private String estacionActual = "ROLLOS";

    // Lista para almacenar y controlar los temporizadores activos en las tarjetas
    private final List<Timeline> activeTimers = new ArrayList<>();

    // --- SERVICIOS Y REPOSITORIOS (Inyección de dependencias de Spring) ---
    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    private ServicioOrden servicioOrden;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    @Lazy
    private ControlVisualizarOrden controlVisualizarOrden;

    /**
     * MÉTODO DE INICIALIZACIÓN:
     * Se ejecuta automáticamente justo después de que JavaFX carga el archivo FXML.
     */
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

    // --- ACCIONES DEL TECLADO NUMÉRICO EN PANTALLA ---

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

        // Limpiamos los temporizadores previos para evitar fugas de memoria o hilos huérfanos
        for (Timeline t : activeTimers) {
            t.stop();
        }
        activeTimers.clear();

        containerPendientes.getChildren().clear();

        for (Pedido pedido : pedidos) {
            List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedido.getIdPedido());
            
            List<DetallesPedido> detallesEstacion = new ArrayList<>();
            if (detalles != null) {
                for (DetallesPedido detalle : detalles) {
                    Platillo platillo = detalle.getPlatillo();
                    if (platillo != null && platillo.getTipoArea() != null && 
                        estacionActual.equalsIgnoreCase(platillo.getTipoArea())) {
                        detallesEstacion.add(detalle);
                    }
                }
            }

            if (!detallesEstacion.isEmpty()) {
                crearTarjetaOrdenConCheckboxes(pedido.getIdPedido(), String.valueOf(pedido.getNumeroOrden()), detallesEstacion);
            }
        }
    }

    private void crearTarjetaOrdenConCheckboxes(Long idPedido, String numeroOrden, List<DetallesPedido> detallesEstacion) {
        VBox card = new VBox(8);
        card.setPrefWidth(240);
        card.setStyle("-fx-background-color: #2D1F21; -fx-background-radius: 8; -fx-border-color: #7B7374; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12px;");

        // Cabecera con número de orden
        Label lblNum = new Label("#" + numeroOrden);
        lblNum.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        // Etiqueta visual para el temporizador en tiempo real
        Label lblTiempo = new Label("Tiempo: 00:00");
        lblTiempo.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFD700; -fx-font-weight: bold;");

        card.getChildren().addAll(lblNum, lblTiempo);

        // Tomamos el momento actual como base para el conteo (puedes cambiarlo por pedido.getHoraCreacion() si tu entidad lo almacena)
        java.time.LocalDateTime horaCreacion = java.time.LocalDateTime.now();

        // Creamos el Timeline que actualizará el texto cada segundo
        Timeline cardTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long segundosTranscurridos = java.time.temporal.ChronoUnit.SECONDS.between(horaCreacion, java.time.LocalDateTime.now());
            long minutos = segundosTranscurridos / 60;
            long segundos = segundosTranscurridos % 60;

            lblTiempo.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));

            // Alerta visual en rojo si la orden supera los 15 minutos
            if (minutos >= 15) {
                lblTiempo.setStyle("-fx-font-size: 12px; -fx-text-fill: #FF4444; -fx-font-weight: bold;");
            }
        }));
        
        cardTimer.setCycleCount(Timeline.INDEFINITE);
        cardTimer.play();
        activeTimers.add(cardTimer); // Registramos el timer

        // Botón Marcar como Terminado
        Button btnFinalizar = new Button("MARCAR COMO TERMINADO");
        btnFinalizar.setDisable(true);
        btnFinalizar.setStyle("-fx-background-color: #E13131; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px;");

        for (DetallesPedido detalle : detallesEstacion) {
            StringBuilder textoItem = new StringBuilder();
            textoItem.append(detalle.getCantidad()).append("x ").append(detalle.getPlatillo().getNombre());
            
            if (detalle.getNotas() != null && !detalle.getNotas().trim().isEmpty()) {
                textoItem.append(" (").append(detalle.getNotas()).append(")");
            }

            CheckBox checkBox = new CheckBox(textoItem.toString());
            checkBox.setWrapText(true);
            checkBox.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px;");
            checkBox.setMinHeight(35);

            checkBox.setSelected(detalle.isCompletado());

            checkBox.setOnAction(e -> {
                detalle.setCompletado(checkBox.isSelected());
                boolean todosListos = detallesEstacion.stream().allMatch(DetallesPedido::isCompletado);
                btnFinalizar.setDisable(!todosListos);
            });

            card.getChildren().add(checkBox);
        }

        boolean todosListosInicial = detallesEstacion.stream().allMatch(DetallesPedido::isCompletado);
        btnFinalizar.setDisable(!todosListosInicial);

        // Al finalizar la orden, detenemos y removemos el temporizador específico de esta tarjeta
        btnFinalizar.setOnAction(e -> {
            cardTimer.stop();
            activeTimers.remove(cardTimer);
            finalizarOrden(idPedido);
        });

        card.getChildren().add(btnFinalizar);
        containerPendientes.getChildren().add(card);
    }
}