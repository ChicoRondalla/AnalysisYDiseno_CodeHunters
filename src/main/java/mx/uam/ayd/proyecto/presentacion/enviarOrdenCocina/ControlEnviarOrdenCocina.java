package mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina;

import java.util.List;

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
import mx.uam.ayd.proyecto.negocio.ServicioOrden;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.presentacion.cancelarOrden.ControlCancelarOrden;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;

@Component
public class ControlEnviarOrdenCocina {

    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    private ServicioOrden servicioOrden;

    @Autowired
    @Lazy
    private ControlPrincipal controlPrincipal; 

    @Autowired
    private VentanaEnviarOrdenCocina ventana;

    @Autowired
    @Lazy
    private ControlVisualizarOrden controlVisualizarOrden;

    @Autowired
    @Lazy
    private ControlCancelarOrden controlCancelarOrden;

    // --- ENLACES FXML ---
    
    @FXML private Button btnCancelarOrden;
    @FXML private Button btnVolver;
    @FXML private Label lblCliente;
    @FXML private Label lblMesa;
    @FXML private Label lblEstado;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    @FXML private TableView<DetallesPedido> tablaDetalle;
    @FXML private TableColumn<DetallesPedido, String> colCategoria;
    @FXML private TableColumn<DetallesPedido, String> colProducto;
    @FXML private TableColumn<DetallesPedido, Integer> colCantidad;
    @FXML private TableColumn<DetallesPedido, Integer> colPrecio;
    @FXML private TableColumn<DetallesPedido, Integer> colSubtotal;

    @FXML private Button btnEnviarCocina;

    private long idPedidoActual;
    private Pedido pedidoActual;

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

    public void inicia(Pedido pedido) {
        this.pedidoActual = pedido;
        if (pedido != null) {
            this.idPedidoActual = pedido.getIdPedido();
        }

        ventana.muestra();
        poblarDatosPedido();
    }

    public void inicia(long idPedido) {
        this.idPedidoActual = idPedido;

        ventana.muestra();

        try {
            this.pedidoActual = servicioPedido.recuperaPedido(idPedidoActual);
            poblarDatosPedido();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void poblarDatosPedido() {
        if (pedidoActual != null) {
            List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedidoActual.getIdPedido());

            if (lblCliente != null) {
                lblCliente.setText(pedidoActual.getCliente() != null ? pedidoActual.getCliente().getNombre() : "Cliente General");
            }
            if (lblMesa != null) {
                lblMesa.setText(String.valueOf(pedidoActual.getNumeroOrden()));
            }
            if (lblEstado != null) {
                lblEstado.setText(pedidoActual.getEstado() != null ? pedidoActual.getEstado() : "Pendiente de envío");
            }

            double total = servicioOrden.calcularSubtotalTotal(pedidoActual.getIdPedido());
            double subtotal = total / 1.16;
            double iva = total - subtotal;

            if (lblSubtotal != null) lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
            if (lblIva != null) lblIva.setText(String.format("IVA: $%.2f", iva));
            if (lblTotal != null) lblTotal.setText(String.format("TOTAL: $%.2f", total));

            if (tablaDetalle != null && detalles != null) {
                tablaDetalle.setItems(FXCollections.observableArrayList(detalles));
                tablaDetalle.refresh();
            }
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
                
                // ABRE Y ACTUALIZA LA VISTA DE COCINA
                if (controlVisualizarOrden != null) {
                    controlVisualizarOrden.inicia();
                }

                mostrarMensajeExito();

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

    /**
     * Método para limpiar la vista visualmente después de una cancelación exitosa
     */
    public void limpiarVistaDespuesDeCancelar() {
        if (lblEstado != null) {
            lblEstado.setText("Cancelada");
        }
        
        if (tablaDetalle != null) {
            tablaDetalle.getItems().clear();
            tablaDetalle.refresh();
        }
        
        if (btnEnviarCocina != null) {
            btnEnviarCocina.setDisable(true);
        }
        if (btnCancelarOrden != null) {
            btnCancelarOrden.setDisable(true);
        }
    }

    @FXML
    public void clickBotonCancelarOrden(ActionEvent event) {
        // 1. Le decimos al sistema que DIBUJE y abra la ventanita roja (como funcionaba antes)
        controlPrincipal.iniciaVentanaCancelarOrden();
        
        // 2. Inmediatamente después, le inyectamos el ID real de la orden actual
        controlCancelarOrden.inicia(idPedidoActual);
    }



    
    @FXML
    public void clickBotonVolver(ActionEvent event) {
        if (btnVolver != null && btnVolver.getScene() != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) btnVolver.getScene().getWindow();
            stage.close();
        }
    }
}