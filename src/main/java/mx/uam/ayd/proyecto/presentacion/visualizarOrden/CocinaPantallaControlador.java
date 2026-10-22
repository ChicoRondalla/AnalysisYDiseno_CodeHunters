package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

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
@Scope("prototype") // Spring crea una nueva instancia (un objeto totalmente nuevo) cada vez que el bean es solicitado o inyectado en otra clase. 

public class CocinaPantallaControlador {

    private static final Logger LOGGER = Logger.getLogger(CocinaPantallaControlador.class.getName());
    private static final String TXT_ORDEN_PREFIX = "Orden #";

    // --- COMPONENTES DE LA VISTA (Inyectados desde FXML) ---
    @FXML
    private Label lblEstacion; // Etiqueta que muestra en qué estación estamos trabajando actualmente ("ROLLOS" o "PLANCHA").

    @FXML
    private FlowPane containerPendientes; // Contenedor en forma de cuadrícula donde se dibujan las tarjetas con las órdenes pendientes.

    @FXML
    private Label txtOrdenInput; // Pantalla pequeña o texto donde se van acumulando los números que el cocinero tipea en el pad numérico.

    @FXML 
    private Label lblTotalTerminados; // Etiqueta que muestra el total de ordenes terminadas 

    @FXML 
    private Label lblPlatillosPendientes; // Etiqueta que muestra el total de platillos por terminar de acuerdo a la estacion seleccionada

    // Estado interno para saber qué área de la cocina está activa por defecto al arrancar.
    private String estacionActual = "ROLLOS";

    // Lista para almacenar y controlar los temporizadores activos en las tarjetas
    private final List<ItemOrdenControlador> controladoresItems = new ArrayList<>();

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

    /**
    * Despliega una ventana modal interactiva que muestra el historial y detalle de todas las órdenes completadas recientemente.
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
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #067919;");

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
                card.setStyle("-fx-background-color: #2D2D2D; -fx-background-radius: 8; -fx-padding: 10px; -fx-border-color: #067919; -fx-border-radius: 8;");

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

    // Método para que el "Director" le diga qué estación cargar al abrirse
    public void setEstacionActual(String estacion) {
        this.estacionActual = estacion;
        actualizarVistaEstacion(); // Actualiza el Label "ESTACIÓN: ROLLOS"[cite: 1]
    }

    private void actualizarVistaEstacion() {
        if (lblEstacion != null) {
            lblEstacion.setText(estacionActual);
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

    /**
    * Finaliza de manera parcial o total los items de un pedido procesados desde una estacion de cocina
    */

    public void finalizarOrden(Long idPedido, List<DetallesPedido> detallesTerminados) {
        try {
            Pedido pedido = servicioPedido.recuperaPedido(idPedido);
            if (pedido != null) {
            
                List<DetallesPedido> todosLosDetalles = servicioOrden.obtenerDetallesDePedido(idPedido);
                boolean todoElPedidoEstaListo = true;
            
                if (todosLosDetalles != null) {
                    for (DetallesPedido detalleGlobal : todosLosDetalles) {
                        boolean esDeEstaEstacion = detallesTerminados.stream()
                            .anyMatch(d -> d.getPlatillo().getNombre().equals(detalleGlobal.getPlatillo().getNombre()));
                        if (esDeEstaEstacion) {
                            detalleGlobal.setCompletado(true);
                            servicioOrden.actualizarDetalle(detalleGlobal);
                        }
                        if (!detalleGlobal.isCompletado()) {
                            todoElPedidoEstaListo = false;
                        }
                    }
                }
                // Solo marcamos el pedido como completado si ya no hay platillos pendientes en ninguna estacion
                if (todoElPedidoEstaListo) {
                    pedido.setEstado("Completado");
                }
                // Guardamos el pedido para que la BD registre el progreso real de las estaciones
                pedidoRepository.save(pedido);
                // Desaparecemos la tarjeta y aumentamos el contador en TODAS las pantallas solo cuando llegue al 100%
                if (todoElPedidoEstaListo && controlVisualizarOrden != null) {
                    controlVisualizarOrden.cargarPedidosPendientes();
                }
            }
        } catch (Exception e) {
            LOGGER.severe(() -> "Error al finalizar la orden: " + e.getMessage());
        }
    }

    /**
    * Recibe la lista general de pedidos pendientes, filtra los elementos correspondientes a la estacion actual
    * y genera dinamicamente las tarjetas visuales (comandas) en pantalla, actualizando los contadores de cola
    */

    public void mostrarPedidosPendientes(List<Pedido> pedidos) {
        if (containerPendientes == null) return;
        for (ItemOrdenControlador c : controladoresItems) {
            c.stopTimer();
        }
        controladoresItems.clear();
        containerPendientes.getChildren().clear();
        int platillosEnCola = 0;
        int pedidosTerminadosDeEstaEstacion = 0;
        for (Pedido pedido : pedidos) {
            List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedido.getIdPedido());
            List<DetallesPedido> detallesEstacion = new ArrayList<>();
            if (detalles != null) {
                for (DetallesPedido detalle : detalles) {
                    Platillo platillo = detalle.getPlatillo();
                    if (platillo != null && platillo.getTipoArea() != null && 
                        estacionActual.equalsIgnoreCase(platillo.getTipoArea())) {
                        detallesEstacion.add(detalle);
                        if (!detalle.isCompletado()) {
                            platillosEnCola += detalle.getCantidad();
                        }
                    }
                }
            }
            if (!detallesEstacion.isEmpty()) {
                crearTarjetaDesdeFXML(pedido.getIdPedido(), String.valueOf(pedido.getNumeroOrden()), detallesEstacion);
            }
        }
        List<Pedido> todosLosCompletados = servicioPedido.recuperaPedidosCompletados();
        if (todosLosCompletados != null) {
            for (Pedido completado : todosLosCompletados) {
                List<DetallesPedido> detallesComp = servicioOrden.obtenerDetallesDePedido(completado.getIdPedido());
                if (detallesComp != null) {
                    boolean perteneceAEstacion = detallesComp.stream()
                        .filter(d -> d.getPlatillo() != null && d.getPlatillo().getTipoArea() != null)
                        .anyMatch(d -> estacionActual.equalsIgnoreCase(d.getPlatillo().getTipoArea()));
                    if (perteneceAEstacion) {
                        pedidosTerminadosDeEstaEstacion++;
                    }
                }
            }
        }
        if (lblPlatillosPendientes != null) {
            lblPlatillosPendientes.setText(String.format("%02d", platillosEnCola));
        }
        if (lblTotalTerminados != null) {
            lblTotalTerminados.setText(String.format("%02d", pedidosTerminadosDeEstaEstacion));
        }
    }

    /**
    * Carga dinmicamente el archivo FXML correspondiente a una tarjeta individual de orden, 
    * inyecta sus datos y la anade al contenedor principal de la vista de cocina
    */

    private void crearTarjetaDesdeFXML(Long idPedido, String numeroOrden, List<DetallesPedido> detallesEstacion) {
        try {
            java.net.URL url = getClass().getResource("/fxml/item-pedido.fxml");
            if(url == null) url = getClass().getResource("/fxml/ItemOrden.fxml");
            if(url == null) {
                LOGGER.severe("No se encontró ItemOrden.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Node tarjetaNode = loader.load();
            ItemOrdenControlador itemControlador = loader.getController();
            itemControlador.setDatos(idPedido, numeroOrden, detallesEstacion, this);
            controladoresItems.add(itemControlador); // Guardamos ref para detener timers luego
            containerPendientes.getChildren().add(tarjetaNode);
        } catch (IOException e) {
            LOGGER.severe("Error cargando ItemOrden.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}