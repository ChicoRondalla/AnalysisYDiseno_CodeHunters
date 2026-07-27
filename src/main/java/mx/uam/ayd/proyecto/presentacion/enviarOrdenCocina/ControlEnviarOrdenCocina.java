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

@Component  // EVITA HACER EL NEW
public class ControlEnviarOrdenCocina {

    @Autowired // AUTOCABLEADO
    private ServicioPedido servicioPedido;

    @Autowired
    private ServicioOrden servicioOrden;

    @Autowired
    @Lazy // CREA EL CONTROLADOR HASTA QUE ES NECESARIO
    private ControlPrincipal controlPrincipal; 

    @Autowired
    private VentanaEnviarOrdenCocina ventana;

    @Autowired
    @Lazy
    private ControlVisualizarOrden controlVisualizarOrden;

    @Autowired
    @Lazy
    private ControlCancelarOrden controlCancelarOrden;

    // --- ENLACES FXML ESTA ENLAZADO DIRECTAMENTE CON LO VISUAL
    
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

    // EL METODO DICE COMO LEER LOS DATOS DE LA ENTIDAD DETALLESPEDIDO
    // DE DETALLESPEDIDO A PLATILLO
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
    // AQUI LLEGA EL ID 
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

    // ESTE LLENA LA PANTALLA
    private void poblarDatosPedido() {
        // TRAE LA LISTA DE PLATILLOS
        if (pedidoActual != null) {
            List<DetallesPedido> detalles = servicioOrden.obtenerDetallesDePedido(pedidoActual.getIdPedido());
            // SET TEX PARA PONER EL NOMBRE ,MESA ETC
            if (lblCliente != null) {
                lblCliente.setText(pedidoActual.getCliente() != null ? pedidoActual.getCliente().getNombre() : "Cliente General");
            }
            if (lblMesa != null) {
                lblMesa.setText(String.valueOf(pedidoActual.getNumeroOrden()));
            }
            if (lblEstado != null) {
                lblEstado.setText(pedidoActual.getEstado() != null ? pedidoActual.getEstado() : "Pendiente de envío");
            }
            // CALCULA EL IVA
            double total = servicioOrden.calcularSubtotalTotal(pedidoActual.getIdPedido());
            double subtotal = total / 1.16;
            double iva = total - subtotal;
            
            if (lblSubtotal != null) lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
            if (lblIva != null) lblIva.setText(String.format("IVA: $%.2f", iva));
            if (lblTotal != null) lblTotal.setText(String.format("TOTAL: $%.2f", total));
            // SE INYECTA LA LISTA DE PLATILLOS ALA TABLA
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
            // CAMINO FELIZ
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
            // SI EL PEDIDO NO EXISTE O YA ESTA EN PREPARACION
        } catch (IllegalArgumentException e) {
            mostrarMensajeError("No se encontró el pedido", e.getMessage());
        } catch (IllegalStateException e) {
            mostrarMensajeError("La orden ya está en preparación y no puede ser modificada", e.getMessage());
        }
    }
    // VENTANAS DE ALAERTA LAS CHICAS
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

     //LIMPIA LA INERFAZ , CAMBIA ESTADO Y LIMPIA TABLA     
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
    // ABRE LA NETANA DE CANCELAR
    @FXML
    public void clickBotonCancelarOrden(ActionEvent event) {
        
        controlPrincipal.iniciaVentanaCancelarOrden();
        controlCancelarOrden.inicia(idPedidoActual);
    }

    // REGRESA ALA VENTANA ANTERIOR 
    @FXML
    public void clickBotonVolver(ActionEvent event) {
        if (btnVolver != null && btnVolver.getScene() != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) btnVolver.getScene().getWindow();
            stage.close();
        }
    }
}