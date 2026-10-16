package mx.uam.ayd.proyecto.presentacion.registrarMesa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioMesa;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Mesa;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.presentacion.armadoOrden.VistaArmadoOrden;
import mx.uam.ayd.proyecto.presentacion.registrarPedido.VistaRegistroPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar la selección de mesas, el plano de sala y sus estados (HU-05).
 */
@Component
public class ControlMesa {

    @Autowired
    private ServicioMesa servicioMesa;

    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    private VistaArmadoOrden vistaArmadoOrden;

    @Autowired
    private VistaRegistroPedido vistaRegistroPedido;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private Label lblEstadoSeleccion;

    @FXML
    private Button btnRegresar;

    // Botones de las mesas del FXML para actualizar su diseño dinámicamente
    @FXML private Button btnMesa1;
    @FXML private Button btnMesa2;
    @FXML private Button btnMesa3;
    @FXML private Button btnMesa4;
    @FXML private Button btnMesa5;

    private int mesaSeleccionadaId = -1;

    /**
     * Carga y muestra la ventana del plano de sala (`PlanoMesas.fxml`).
     */
    public void inicia() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PlanoMesas.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            
            Parent root = fxmlLoader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ryuho Sushi - Plano de Sala");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se ejecuta automáticamente al cargar la vista para pintar las tarjetas con el estado real de la BD.
     */
    @FXML
    public void initialize() {
        actualizarVistaMesas();
    }

    private void actualizarVistaMesas() {
        List<Mesa> mesas = servicioMesa.obtenerMesas();
        for (Mesa m : mesas) {
            Button btnTarget = obtenerBotonPorNumero(m.getNumeroMesa());
            if (btnTarget != null) {
                String estado = m.getEstado();
                btnTarget.setText("MESA " + m.getNumeroMesa() + "\nCapacidad: " + m.getCapacidad() + " pers.\n" + estado);
                
                // Color rojo oscuro si está ocupada, gris oscuro si está libre
                if ("OCUPADA".equalsIgnoreCase(estado)) {
                    btnTarget.setStyle("-fx-background-color: #7f1d1d; -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 12; -fx-text-fill: white;");
                } else {
                    btnTarget.setStyle("-fx-background-color: #212126; -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-text-fill: white;");
                }
            }
        }
    }

    private Button obtenerBotonPorNumero(int numero) {
        switch (numero) {
            case 1: return btnMesa1;
            case 2: return btnMesa2;
            case 3: return btnMesa3;
            case 4: return btnMesa4;
            case 5: return btnMesa5;
            default: return null;
        }
    }

    @FXML
    public void handleSeleccionarMesa1() { seleccionarMesaLogica(1); }
    @FXML
    public void handleSeleccionarMesa2() { seleccionarMesaLogica(2); }
    @FXML
    public void handleSeleccionarMesa3() { seleccionarMesaLogica(3); }
    @FXML
    public void handleSeleccionarMesa4() { seleccionarMesaLogica(4); }
    @FXML
    public void handleSeleccionarMesa5() { seleccionarMesaLogica(5); }

    private void seleccionarMesaLogica(int numeroMesa) {
        mesaSeleccionadaId = numeroMesa;
        Optional<Mesa> mesaOpt = servicioMesa.obtenerMesaPorNumero(numeroMesa);
        if(mesaOpt.isPresent()) {
            Mesa m = mesaOpt.get();
            if (lblEstadoSeleccion != null) {
                // Muestra la información detallada completa en la barra inferior
                lblEstadoSeleccion.setText("Mesa " + m.getNumeroMesa() + 
                    "  |  Capacidad: " + m.getCapacidad() + " personas" + 
                    "  |  Estado: " + m.getEstado());
            }
        }
    }

    @FXML
    public void handleRegistrarPedido() {
        if (mesaSeleccionadaId != -1) {
            Optional<Mesa> mesaOpt = servicioMesa.obtenerMesaPorNumero(mesaSeleccionadaId);
            if (mesaOpt.isPresent()) {
                Mesa mesa = mesaOpt.get();
                
                if (!mesa.isLibre()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Mesa Ocupada", 
                        "La Mesa " + mesaSeleccionadaId + " ya se encuentra ocupada con un pedido en curso.");
                    return;
                }
                
                servicioMesa.actualizarEstadoMesa(mesaSeleccionadaId, "OCUPADA");
                Pedido nuevoPedido = servicioPedido.crearPedidoLocal(mesaSeleccionadaId);
                
                actualizarVistaMesas();
                
                mostrarAlerta(Alert.AlertType.INFORMATION, "Mesa Registrada", 
                    "La Mesa " + mesaSeleccionadaId + " se ha seleccionado correctamente.");
                
                cerrarVentana();
                vistaArmadoOrden.muestra(nuevoPedido);
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Primero debe seleccionar una mesa.");
        }
    }

    @FXML
    public void handleCambiarEstado() {
        if (mesaSeleccionadaId != -1) {
            Optional<Mesa> mesaOpt = servicioMesa.obtenerMesaPorNumero(mesaSeleccionadaId);
            mesaOpt.ifPresent(m -> {
                String nuevoEstado = m.isLibre() ? "OCUPADA" : "LIBRE";
                servicioMesa.actualizarEstadoMesa(mesaSeleccionadaId, nuevoEstado);
                seleccionarMesaLogica(mesaSeleccionadaId);
                actualizarVistaMesas();
            });
        }
    }

    @FXML
    public void handleRegresar() {
        cerrarVentana();
        vistaRegistroPedido.muestra(applicationContext.getBean(mx.uam.ayd.proyecto.presentacion.registrarPedido.ControlRegistroPedido.class));
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = null;
        if (btnRegresar != null && btnRegresar.getScene() != null) {
            stage = (Stage) btnRegresar.getScene().getWindow();
        } else if (lblEstadoSeleccion != null && lblEstadoSeleccion.getScene() != null) {
            stage = (Stage) lblEstadoSeleccion.getScene().getWindow();
        }
        
        if (stage != null) {
            stage.close();
        }
    }
}