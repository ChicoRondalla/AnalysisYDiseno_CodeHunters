package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.List;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

/**
 * ControlVisualizarOrden: El director de orquesta del caso de uso.
 * ¿Qué papel juega? Esta clase actúa como el puente (o coordinador) entre la lógica de negocio 
 * del sistema y las pantallas gráficas de la cocina. No dibuja botones directamente, 
 * sino que decide *cuándo* hay que abrir la ventana y *cuándo* hay que actualizar los pedidos que aparecen en ella.
 */
@Component
public class ControlVisualizarOrden {

    // Referencia a la clase encargada de gestionar la ventana visual de la cocina.
    private final VistaCocinaPantalla vistaCocinaPantalla;

    // --- SERVICIOS Y CONTROLADORES INYECTADOS POR SPRING ---
    
    @Autowired
    private ServicioPedido servicioPedido; // Nos permite consultar los pedidos directamente desde las reglas de negocio/base de datos.

    @Autowired
    private CocinaPantallaControlador cocinaPantallaControlador; // Controlador visual (el que vimos en el archivo anterior) que manipula los elementos de la pantalla.

    /**
     * CONSTRUCTOR PRINCIPAL:
     * ¿Cuándo se llama? Cuando Spring crea este componente e inyecta automáticamente la ventana de cocina necesaria.
     */
    @Autowired
    public ControlVisualizarOrden(VistaCocinaPantalla vistaCocinaPantalla) {
        this.vistaCocinaPantalla = vistaCocinaPantalla;
    }

    /**
     * INICIA EL FLUJO DE LA PANTALLA DE COCINA:
     * ¿Cuándo se llama? Se ejecuta cuando el usuario decide abrir la sección de cocina desde el menú principal del sistema.
     * Es el punto de arranque que despliega la interfaz gráfica.
     */
    public void inicia() {
        // 1. Mostrar la ventana física en la pantalla utilizando el hilo principal de JavaFX.
        vistaCocinaPantalla.muestra(this);

        // 2. Dar tiempo a JavaFX de realizar el enlace (binding) de los componentes del archivo FXML 
        // antes de intentar inyectar datos en ellos, evitando errores de elementos nulos (NullPointerException).
        Platform.runLater(() -> {
            cargarPedidosPendientes();
        });
    }

    /**
     * CARGA Y DISTRIBUYE LOS PEDIDOS PENDIENTES:
     * ¿Cuándo se llama? Se invoca justo al arrancar la pantalla (vía `inicia`), cada vez que el cocinero cambia de estación 
     * (Rollos / Plancha) o cuando una orden cambia de estado.
     * Su trabajo es ir a la base de datos a preguntar qué hay pendiente y pasárselo al controlador visual para que lo dibuje.
     */
    public void cargarPedidosPendientes() {
        try {
            // Consultamos al servicio de negocio la lista actualizada de todos los pedidos que siguen pendientes de entrega.
            List<Pedido> pedidosPendientes = servicioPedido.recuperaPedidosPendientes();
            
            System.out.println(">>> [COCINA] Pedidos recuperados para cocina: " + 
                (pedidosPendientes != null ? pedidosPendientes.size() : 0));
            
            // Si la consulta fue exitosa, le entregamos la lista al controlador de la pantalla para que dibuje las tarjetas con checkboxes.
            if (pedidosPendientes != null) {
                cocinaPantallaControlador.mostrarPedidosPendientes(pedidosPendientes);
            }
        } catch (Exception e) {
            System.err.println(">>> Error al cargar pedidos pendientes en cocina: " + e.getMessage());
            e.printStackTrace();
        }
    }
}