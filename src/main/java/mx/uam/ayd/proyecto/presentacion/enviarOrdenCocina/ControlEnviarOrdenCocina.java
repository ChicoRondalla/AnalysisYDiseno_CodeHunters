package mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;

@Component
public class ControlEnviarOrdenCocina {

    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    @Lazy
    private ControlPrincipal controlPrincipal; 

    @Autowired
    private VentanaEnviarOrdenCocina ventana;

    @Autowired
    private ControlVisualizarOrden controlVisualizarOrden;

    // --- ENLACES FXML ---
    
    @FXML
    private Button btnCancelarOrden;

    @FXML
    private Button btnVolver;

    @FXML
    private Label lblCliente;

    @FXML
    private Label lblMesa;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblIva;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<DetallesPedido> tablaDetalle;

    @FXML
    private TableColumn<DetallesPedido, String> colCategoria;

    @FXML
    private TableColumn<DetallesPedido, String> colProducto;

    @FXML
    private TableColumn<DetallesPedido, Integer> colCantidad;

    @FXML
    private TableColumn<DetallesPedido, Integer> colPrecio;

    @FXML
    private TableColumn<DetallesPedido, Integer> colSubtotal;

    @FXML
    private Button btnEnviarCocina;

    private long idPedidoActual;

    @FXML
    public void initialize() {
        configurarColumnas();
    }

    private void configurarColumnas() {
        if (colCategoria != null) {
            colCategoria.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getPlatillo() != null ? 
                    cellData.getValue().getPlatillo().getTipoArea() : "N/A"));
        }
        if (colProducto != null) {
            colProducto.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getPlatillo() != null ? 
                    cellData.getValue().getPlatillo().getNombre() : "Sin Nombre"));
        }
        if (colCantidad != null) {
            colCantidad.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        }
        if (colPrecio != null) {
            colPrecio.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getPlatillo() != null ? 
                    cellData.getValue().getPlatillo().getPrecio() : 0).asObject());
        }
        if (colSubtotal != null) {
            colSubtotal.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getSubtotal()).asObject());
        }
    }

    /**
     * Inicia la ventana recuperando y cargando el pedido recibido.
     */
    public void inicia(long idPedido) {
        System.out.println(">>> [CONTROL ENVIAR COCINA] Iniciando con ID de Pedido: " + idPedido);
        this.idPedidoActual = idPedido;

        // 1. Desplegamos la ventana
        ventana.muestra();

        // 2. Cargamos los datos del pedido en las etiquetas y tabla
        cargarDatosPedido();
    }

    /**
     * Obtiene el Pedido desde la base de datos y llena los componentes gráficos.
     */
    private void cargarDatosPedido() {
        try {
            Pedido pedido = servicioPedido.recuperaPedido(idPedidoActual);

            if (pedido != null) {
                System.out.println(">>> [CONTROL ENVIAR COCINA] Pedido cargado con exito. Total detalles: " + 
                    (pedido.getDetallesPedido() != null ? pedido.getDetallesPedido().size() : 0));

                if (lblCliente != null) {
                    lblCliente.setText(pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Cliente General");
                }
                if (lblMesa != null) {
                    lblMesa.setText(String.valueOf(pedido.getNumeroOrden()));
                }
                if (lblEstado != null) {
                    lblEstado.setText(pedido.getEstado() != null ? pedido.getEstado() : "Pendiente de envío");
                }

                double total = pedido.getTotal();
                double subtotal = total / 1.16;
                double iva = total - subtotal;

                if (lblSubtotal != null) lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
                if (lblIva != null) lblIva.setText(String.format("IVA: $%.2f", iva));
                if (lblTotal != null) lblTotal.setText(String.format("TOTAL: $%.2f", total));

                if (tablaDetalle != null && pedido.getDetallesPedido() != null) {
                    tablaDetalle.setItems(FXCollections.observableArrayList(pedido.getDetallesPedido()));
                    tablaDetalle.refresh();
                }
            } else {
                System.err.println(">>> [ERROR] El pedido con ID " + idPedidoActual + " devolvió NULL desde ServicioPedido.");
            }
        } catch (Exception e) {
            System.err.println(">>> [EXCEPCION] Error al cargar la información de la orden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void clickBotonEnviar(ActionEvent event) {
        try {
            boolean exito = servicioPedido.procesarEnvioCocina(idPedidoActual);
            
            if (exito) {
                if (lblEstado != null) {
                    lblEstado.setText("En Preparación");
                }
                mostrarMensajeExito();

                // 1. Iniciar el controlador de la cocina (pantalla rojiza)
                if (controlVisualizarOrden != null) {
                    controlVisualizarOrden.inicia();
                }

                // 2. Cerrar la ventana del resumen actual
                if (btnEnviarCocina != null && btnEnviarCocina.getScene() != null) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) btnEnviarCocina.getScene().getWindow();
                    stage.close();
                }
            }
        } catch (IllegalArgumentException e) {
            mostrarMensajeError("No se encontró el pedido", e.getMessage());
        } catch (IllegalStateException e) {
            mostrarMensajeError("La orden ya está en preparación y no puede ser modificada", e.getMessage());
        }
    }
    
    private void mostrarMensajeExito() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("¡La orden ha sido enviada exitosamente a la cocina!");
        alert.showAndWait();
    }

    private void mostrarMensajeError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void clickBotonCancelarOrden(ActionEvent event) {
        controlPrincipal.iniciaVentanaCancelarOrden();
    }
    
    @FXML
    public void clickBotonVolver(ActionEvent event) {
        if (btnVolver != null && btnVolver.getScene() != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) btnVolver.getScene().getWindow();
            stage.close();
        }
    }
}