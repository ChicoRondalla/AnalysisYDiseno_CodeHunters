package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.List;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

/**
 * Control del caso de uso: Visualizar Orden / Comanda en Cocina
 */
@Component
public class ControlVisualizarOrden {

    private final VistaCocinaPantalla vistaCocinaPantalla;

    @Autowired
    private ServicioPedido servicioPedido;

    @Autowired
    private CocinaPantallaControlador cocinaPantallaControlador;

    @Autowired
    public ControlVisualizarOrden(VistaCocinaPantalla vistaCocinaPantalla) {
        this.vistaCocinaPantalla = vistaCocinaPantalla;
    }

    /**
     * Inicia la ventana de cocina y carga las órdenes activas.
     */
    public void inicia() {
        // 1. Mostrar la ventana en el hilo de JavaFX
        vistaCocinaPantalla.muestra(this);

        // 2. Dar tiempo a JavaFX de realizar el binding del FXML antes de cargar datos
        Platform.runLater(() -> {
            cargarPedidosPendientes();
        });
    }

    /**
     * Consulta los pedidos desde la base de datos y los envía al controlador FXML.
     */
    public void cargarPedidosPendientes() {
        try {
            List<Pedido> pedidosPendientes = servicioPedido.recuperaPedidosPendientes();
            System.out.println(">>> [COCINA] Pedidos recuperados para cocina: " + 
                (pedidosPendientes != null ? pedidosPendientes.size() : 0));
            
            if (pedidosPendientes != null) {
                cocinaPantallaControlador.mostrarPedidosPendientes(pedidosPendientes);
            }
        } catch (Exception e) {
            System.err.println(">>> Error al cargar pedidos pendientes en cocina: " + e.getMessage());
            e.printStackTrace();
        }
    }
}