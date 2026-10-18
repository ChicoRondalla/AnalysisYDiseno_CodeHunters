package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.presentacion.armadoOrden.VistaArmadoOrden;

/**
 * Controlador para la Historia de Usuario HU-01: Registrar Pedido.
 */
@Component
public class ControlRegistroPedido {

    // --- PANES (Las diferentes "pantallas") ---
    @FXML private HBox paneSeleccion;
    @FXML private Pane paneDomicilio;
    @FXML private Pane paneLocal;
    @FXML private Pane paneRecoger;

    // --- CAMPOS DE TEXTO (Formularios) ---
    @FXML private TextField txtNombreDomicilio;
    @FXML private TextField txtTelefonoDomicilio;
    @FXML private TextField txtDireccionDomicilio;
    
    @FXML private TextField txtNombreRecoger;
    @FXML private TextField txtTelefonoRecoger;

    // Inyectamos la capa de negocio para las validaciones
    @Autowired
    private ServicioPedido servicioPedido;

    // Inyectamos la nueva ventana de Armado de Orden
    @Autowired
    private VistaArmadoOrden vistaArmadoOrden;


    // Inyectamos la ventana que realmente debería tener el control
    @Autowired 
    private VistaRegistroPedido ventana;

    // --- MÉTODO AUXILIAR PARA NAVEGACIÓN ---
    /**
     * Oculta todos los paneles y muestra únicamente el que se pasa por parámetro.
     */
    private void mostrarPantalla(Pane pantallaActiva) {
        paneSeleccion.setVisible(false);
        paneDomicilio.setVisible(false);
        paneLocal.setVisible(false);
        paneRecoger.setVisible(false);
        
        cargarPedidos(); // Se llama a la función encargada de mostrar a los pedidos cada que se muestre la pantalla
        pantallaActiva.setVisible(true);
    }

    // --- MÉTODOS DE NAVEGACIÓN (Botones principales) ---

    @FXML
    void onConsumoLocalAction(ActionEvent event) {
        mostrarPantalla(paneLocal);
    }

    @FXML
    void onParaRecogerAction(ActionEvent event) {
        mostrarPantalla(paneRecoger);
    }

    @FXML
    void onDomicilioAction(ActionEvent event) {
        mostrarPantalla(paneDomicilio);
    }

    @FXML
    void onRegresarAction(ActionEvent event) {
        // Al regresar, limpiamos los campos por si el usuario había escrito algo
        limpiarCampos();
        mostrarPantalla(paneSeleccion);
    }

    // --- MÉTODOS DE VALIDACIÓN Y CONTINUACIÓN ---

    @FXML
    void onContinuarDomicilioAction(ActionEvent event) {
        String nombre = txtNombreDomicilio.getText();
        String telefono = txtTelefonoDomicilio.getText();
        String direccion = txtDireccionDomicilio.getText();

        boolean datosValidos = servicioPedido.validarDatosDomicilio(nombre, telefono, direccion);

        if (datosValidos) {
            // Atrapamos el pedido recién creado
            Pedido nuevoPedido = servicioPedido.crearPedidoDomicilio(nombre, telefono, direccion);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Registrado", "El pedido a domicilio se guardó correctamente en el sistema.");
            limpiarCampos();
            mostrarPantalla(paneSeleccion);
            
            // ¡Abrimos la pantalla del menú y le pasamos el ticket!
            vistaArmadoOrden.muestra(nuevoPedido);
            cerrarVentana();
            
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", 
                "Verifica que ningún campo esté vacío y que el teléfono tenga exactamente 10 dígitos.");
        }
    }

    @FXML
    void onContinuarRecogerAction(ActionEvent event) {
        String nombre = txtNombreRecoger.getText();
        String telefono = txtTelefonoRecoger.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Falta información", "El nombre del cliente es obligatorio.");
        } else {
            // Atrapamos el pedido recién creado
            Pedido nuevoPedido = servicioPedido.crearPedidoRecoger(nombre, telefono);
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido Registrado", "El pedido para recoger se guardó correctamente en el sistema.");
            limpiarCampos();
            mostrarPantalla(paneSeleccion);
            
            // ¡Abrimos la pantalla del menú y le pasamos el ticket!
            vistaArmadoOrden.muestra(nuevoPedido);
            cerrarVentana();
        }
    }

    // --- MÉTODOS UTILITARIOS ---

    private void limpiarCampos() {
        txtNombreDomicilio.clear();
        txtTelefonoDomicilio.clear();
        txtDireccionDomicilio.clear();
        txtNombreRecoger.clear();
        txtTelefonoRecoger.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // --- MÉTODOS PARA CONSUMO LOCAL (Mesas) ---

    /**
     * Método auxiliar para registrar la mesa seleccionada, 
     * mostrar la alerta y regresar al menú.
     */
    private void registrarPedidoMesa(int numeroMesa) {
        // Atrapamos el pedido recién creado (AQUÍ ESTABA EL DETALLE)
        Pedido nuevoPedido = servicioPedido.crearPedidoLocal(numeroMesa);
        
        mostrarAlerta(Alert.AlertType.INFORMATION, "Mesa registrada", 
            "La Mesa " + numeroMesa + " se ha seleccionado correctamente.");
            
        mostrarPantalla(paneSeleccion);

        // ¡Abrimos la pantalla del menú y le pasamos el ticket!
            vistaArmadoOrden.muestra(nuevoPedido);
            cerrarVentana();
    }

    @FXML
    void onMesa1Action(ActionEvent event) {
        registrarPedidoMesa(1);
    }

    @FXML
    void onMesa2Action(ActionEvent event) {
        registrarPedidoMesa(2);
    }

    @FXML
    void onMesa3Action(ActionEvent event) {
        registrarPedidoMesa(3);
    }

    @FXML
    void onMesa4Action(ActionEvent event) {
        registrarPedidoMesa(4);
    }

    @FXML
    void onMesa5Action(ActionEvent event) {
        registrarPedidoMesa(5);
    }

    /**
     * Obtiene la ventana actual a través de uno de los paneles y la cierra.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) paneSeleccion.getScene().getWindow();
        stage.close();
    }

    /**
     * El método se encarga de pedir a Servicio los pedidos completados para mandárselos a la vista y que ella se encargue de mostrarlos, o en su defecto, informe que no hay pedidos
     * @author JLCB
     */
    public void cargarPedidos(){
        List<Pedido> pedidosCompletados = servicioPedido.recuperaPedidosCompletados();
        
        // Dependiendo de si existen o no pedidos completados, se manda a mostrar un mensaje o a mostrar los pedidos
        if(pedidosCompletados.isEmpty()){
            this.dibujarNingunaTarjetaPedido(); // ventana.dibujarNingunaTarjetaPedido();
        }else{
            this.dibujarTarjetasPedidos(pedidosCompletados); // ventana.dibujarNingunaTarjetaPedido();
        }
    }

    // SECCIÓN DE CÓDIGO QUE DEBERÍA PERTENECER A LA VISTA. Debido a la arquitectura implementada se debe colocar aquí para evitar fallos
    // Elementos de la tarjeta
    @FXML
    private VBox cajaDePedidos;
    // Label no hay pedidos
    @FXML
    private Label noPedidosCompletos;
    
    /**
     * Método que recibe la lista de pedidos dada por control y muestra a todos los pedidos que se encuentren con ayuda de ControlPlantillaPedidoCompletado para dar detalles a cada uno
     * @author JLCB
     * @param listaPedidos Es la lista de pedidos completados y, por lo tanto, pedidos que necesitamos mostrar
     */
    private void dibujarTarjetasPedidos(List<Pedido> listaPedidos){
        // Oculta el mensaje que dice que no hay pedidos
        noPedidosCompletos.setManaged(false);
        noPedidosCompletos.setVisible(false);
        noPedidosCompletos.setDisable(true);
        // Muestra el contenedor que contiene a los mensajes
        cajaDePedidos.setManaged(true);
        cajaDePedidos.setVisible(true);
        cajaDePedidos.setDisable(false);

        cajaDePedidos.getChildren().clear(); // "Prepara" la caja en la que estarán las tarjetas
        for(Pedido pedido : listaPedidos){
            try{
                // Carga a la plantilla de la tarjeta de los pedidos
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/plantilla-pedido-completado.fxml"));
                ControlPlantillaPedidoCompletado tarjeta = new ControlPlantillaPedidoCompletado(); // Se asigna el controlador hecho a la tarjeta
                tarjeta.setControl(this); // tarjeta.setControl(controlRegistroPedido); // Se da que este controlador tiene el control sobre las tarjetas, para apoyar la comunicación con los servicios
                loader.setController(tarjeta);
                Node tarjetaVisual = loader.load(); // Se inicializa la tarjeta
                tarjeta.configurarDatos(pedido); // Mediante el controlador se llama al método para ingresar la información
                cajaDePedidos.getChildren().add(tarjetaVisual); // Se agrega la tarjeta al contenedor
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * Método dado en caso de que la lista de pedido completados esté vacía. Se encarga de mostrar el mensaje que informa que no hay pedidos completados y oculta al contenedor de los pedidos para evitar errores
     * @author JLCB
     */
    public void dibujarNingunaTarjetaPedido(){
        // Muestra el mensaje de ningún pedido completado
        noPedidosCompletos.setManaged(true);
        noPedidosCompletos.setVisible(true);
        noPedidosCompletos.setDisable(false);
        // Oculta el contenedor de pedidos completados
        cajaDePedidos.setManaged(false);
        cajaDePedidos.setVisible(false);
        cajaDePedidos.setDisable(true);
    }
    /**
     * Pantalla emergente que se abre para informar que un pedido efectuó el cambio en su atributo de manera correcta
     * @author JLCB
     * @param pedido
     */
    public void alertaMarcadoComoEntregado(){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Pedido entregado.");
        alerta.setHeaderText(null);
        alerta.setContentText("El pedido se ha marcado como entregado de manera exitosa.");
        alerta.showAndWait();
        System.out.println("Se mostró el mensaje de éxito al ser entregado");
    }
    /**
     * Pantalla emergente que se abre para informar que ocurrió un error al momento de efectuar el cambio en el atributo
     * @author JLCB
     * @param pedido
     */
    public void alertaErrorAlEntregar(){
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Error al marcar como entregado.");
        alerta.setHeaderText(null);
        alerta.setContentText("El pedido no se ha podido marcar como entregado.");
        alerta.showAndWait();
        System.out.println("Se mostró el mensaje de error al ser entregado");
    }
    // FIN DE LA SECCIÓN

    /**
     * Método encargado de marcar como entregado cierto pedido específico comunicandose con el servicio
     * @author JLCB
     */
    public void marcarComoEntregado(Pedido pedido){
        if(servicioPedido.marcarComoEntregado(pedido)){
            alertaMarcadoComoEntregado(); // ventana.alertaMarcadoComoEntregado();
        }else{
            alertaErrorAlEntregar(); // ventana.alertaErrorAlEntregar();
        }
    }
}