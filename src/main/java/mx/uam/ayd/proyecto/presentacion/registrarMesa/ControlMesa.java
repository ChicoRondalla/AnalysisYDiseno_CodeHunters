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

// Controlador para gestion de mesas, el plano de sala y sus estados (HU-05).
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

    @FXML private Label lblEstadoSeleccion;
    @FXML private Button btnRegresar;

    @FXML private Button btnRegistrarPedido;
    @FXML private Button btnReservar;

    // Botones individualmente para cada mesa en la interfaz FXML
    @FXML private Button btnMesa1;
    @FXML private Button btnMesa2;
    @FXML private Button btnMesa3;
    @FXML private Button btnMesa4;
    @FXML private Button btnMesa5;

    // Variable privada que almacena el identificador numérico de la mesa seleccionada actualmente (-1 si no hay ninguna)
    private int mesaSeleccionadaId = -1;

    // Método encargado de inicializar, cargar y desplegar la ventana gráfica del plano de sala
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

    // Método de inicialización automática ejecutado por JavaFX al cargar los componentes del controlador
    @FXML
    public void initialize() {
        actualizarVistaMesas();
        if (btnRegistrarPedido != null) btnRegistrarPedido.setDisable(true);
        if (btnReservar != null) btnReservar.setDisable(true);
    }

    // Método privado para actualizar dinámicamente los textos, capacidades y colores de las mesas según su estado actual en la base de datos
    private void actualizarVistaMesas() {
        List<Mesa> mesas = servicioMesa.obtenerMesas();
        for (Mesa m : mesas) {
            Button btnTarget = obtenerBotonPorNumero(m.getNumeroMesa());
            if (btnTarget != null) {
                String estado = m.getEstado();
                btnTarget.setText("MESA " + m.getNumeroMesa() + "\nCapacidad: " + m.getCapacidad() + " pers.\n" + estado);
                if ("OCUPADA".equalsIgnoreCase(estado)) {
                    btnTarget.setStyle("-fx-background-color: #7f1d1d; -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 12; -fx-text-fill: white;");
                } else if ("RESERVADA".equalsIgnoreCase(estado)) {
                    btnTarget.setStyle("-fx-background-color: #ca8a04; -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 12; -fx-text-fill: white;");
                } else {
                    btnTarget.setStyle("-fx-background-color: #212126; -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-text-fill: white;");
                }
            }
        }
    }

    // Método para mapear el entero de una mesa con su respectivo objeto Button
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

    // Métodos manejadores de eventos vinculados directamente a la selección gráfica de cada mesa en la interfaz
    @FXML public void handleSeleccionarMesa1() { seleccionarMesaLogica(1); }
    @FXML public void handleSeleccionarMesa2() { seleccionarMesaLogica(2); }
    @FXML public void handleSeleccionarMesa3() { seleccionarMesaLogica(3); }
    @FXML public void handleSeleccionarMesa4() { seleccionarMesaLogica(4); }
    @FXML public void handleSeleccionarMesa5() { seleccionarMesaLogica(5); }

    // Método central de lógica que procesa la selección de una mesa, actualiza las etiquetas informativas y habilita o deshabilita los botones de acción
    private void seleccionarMesaLogica(int numeroMesa) {
        // Almacena el identificador de la mesa seleccionada
        mesaSeleccionadaId = numeroMesa;
        // Busca la información detallada de la mesa en el servicio correspondiente
        Optional<Mesa> mesaOpt = servicioMesa.obtenerMesaPorNumero(numeroMesa);
        if(mesaOpt.isPresent()) {
            Mesa m = mesaOpt.get();
            String estado = m.getEstado();
            // Actualiza la etiqueta de texto inferior con los detalles de la mesa seleccionada
            if (lblEstadoSeleccion != null) {
                lblEstadoSeleccion.setText("Mesa " + m.getNumeroMesa() + 
                    "  |  Capacidad: " + m.getCapacidad() + " personas" + 
                    "  |  Estado: " + estado);
            }
            // Configura el comportamiento y texto dinámico del botón de reserva/liberación según el estado actual de la mesa
            if (btnReservar != null) {
                btnReservar.setDisable(false);
                if ("RESERVADA".equalsIgnoreCase(estado)) {
                    btnReservar.setText("Cancelar Reserva");
                } else if ("OCUPADA".equalsIgnoreCase(estado)) {
                    btnReservar.setText("Liberar Mesa");
                } else {
                    btnReservar.setText("Reservar Mesa");
                }
            }
            // Habilita o deshabilita el botón de registrar pedido dependiendo de si la mesa se encuentra libre o reservada
            if (btnRegistrarPedido != null) {
                boolean permitirPedido = "LIBRE".equalsIgnoreCase(estado) || "RESERVADA".equalsIgnoreCase(estado);
                btnRegistrarPedido.setDisable(!permitirPedido);
            }
        }
    }

    // Método ejecutado al hacer clic en el botón de registrar pedido para iniciar una orden y cambiar el estado de la mesa a ocupada
    @FXML
    public void handleRegistrarPedido() {
        if (mesaSeleccionadaId != -1) {
            Optional<Mesa> mesaOpt = servicioMesa.obtenerMesaPorNumero(mesaSeleccionadaId);
            if (mesaOpt.isPresent()) {
                Mesa mesa = mesaOpt.get();
                // Valida si la mesa ya se encuentra ocupada para impedir duplicar órdenes
                if ("OCUPADA".equalsIgnoreCase(mesa.getEstado())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Mesa Ocupada", 
                        "La Mesa " + mesaSeleccionadaId + " ya se encuentra ocupada con un pedido en curso.");
                    return;
                }
                // Actualiza el estado de la mesa a OCUPADA y crea un nuevo registro de pedido local
                servicioMesa.actualizarEstadoMesa(mesaSeleccionadaId, "OCUPADA");
                Pedido nuevoPedido = servicioPedido.crearPedidoLocal(mesaSeleccionadaId);
                // Refresca la selección lógica y la representación visual de las mesas
                seleccionarMesaLogica(mesaSeleccionadaId);
                actualizarVistaMesas();
                // Muestra una alerta informativa confirmando el registro de la mesa y la orden
                mostrarAlerta(Alert.AlertType.INFORMATION, "Mesa Registrada", 
                    "La Mesa " + mesaSeleccionadaId + " ha sido ocupada y se ha registrado su orden.");
                // Abre la vista correspondiente para el armado y captura de los platillos del pedido
                vistaArmadoOrden.muestra(nuevoPedido);
            }
        } else {
            // Muestra una advertencia en caso de que el usuario intente registrar un pedido sin seleccionar una mesa
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Primero debe seleccionar una mesa.");
        }
    }

    // Método multifuncional para gestionar de forma interactiva las reservas, cancelaciones de reservas y liberación de mesas ocupadas
    @FXML
    public void handleReservar() {
        if (mesaSeleccionadaId != -1) {
            Optional<Mesa> mesaOpt = secureMesaOpt(mesaSeleccionadaId);
            if (mesaOpt.isPresent()) {
                Mesa m = mesaOpt.get();
                String estado = m.getEstado();
                if ("OCUPADA".equalsIgnoreCase(estado)) {
                    // Cambia el estado de una mesa ocupada a LIBRE una vez que los clientes se retiran
                    servicioMesa.actualizarEstadoMesa(mesaSeleccionadaId, "LIBRE");
                    seleccionarMesaLogica(mesaSeleccionadaId);
                    actualizarVistaMesas();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Mesa Liberada", 
                        "La Mesa " + mesaSeleccionadaId + " ha sido liberada exitosamente y ya está disponible.");
                } else if ("RESERVADA".equalsIgnoreCase(estado)) {
                    // Cancela una reserva existente devolviendo la mesa al estado LIBRE
                    servicioMesa.actualizarEstadoMesa(mesaSeleccionadaId, "LIBRE");
                    seleccionarMesaLogica(mesaSeleccionadaId);
                    actualizarVistaMesas();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Cancelada", 
                        "La reserva de la Mesa " + mesaSeleccionadaId + " ha sido cancelada.");
                } else {
                    // Aparta una mesa libre cambiando su estado a RESERVADA
                    servicioMesa.actualizarEstadoMesa(mesaSeleccionadaId, "RESERVADA");
                    seleccionarMesaLogica(mesaSeleccionadaId);
                    actualizarVistaMesas();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Mesa Reservada", 
                        "La Mesa " + mesaSeleccionadaId + " ha sido reservada exitosamente.");
                }
            }
        } else {
            // Muestra una advertencia si se acciona el botón sin haber seleccionado previamente una mesa
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Primero debe seleccionar una mesa.");
        }
    }

    // Método para recuperar la entidad Mesa envuelta en un Optional
    private Optional<Mesa> secureMesaOpt(int id) {
        return servicioMesa.obtenerMesaPorNumero(id);
    }

    // Método ejecutado al pulsar el botón de regresar para cerrar la ventana actual y retornar al registro de pedidos
    @FXML
    public void handleRegresar() {
        cerrarVentana();
        vistaRegistroPedido.muestra(applicationContext.getBean(mx.uam.ayd.proyecto.presentacion.registrarPedido.ControlRegistroPedido.class));
    }

    // Método para dar cuadros de diálogo de alerta al usuario
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Método para cerrar la ventana gráfica :b
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