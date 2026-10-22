package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

/**
* Controlador de presentacion (Visualizar Orden / Tablero de Cocina)
* Se encarga de gestionar el flujo de navegacion desde la seleccion de estacion de trabajo
* hasta la apertura y sincronizacion multiventana de las pantallas de cocina 
*/

@Component
public class ControlVisualizarOrden {

    @Autowired private VistaSeleccionEstacion vistaSeleccionEstacion;
    @Autowired private ServicioPedido servicioPedido;
    
    // El contexto nos sirve como "fabrica" para crear ventanas nuevas
    @Autowired private ApplicationContext context; 

    // Lista para guardar todas las ventanas que el usuario abra
    private final List<CocinaPantallaControlador> ventanasAbiertas = new ArrayList<>();

    /**
     * Inicia el flujo del modulo mostrando la pantalla principal de selección de estación.
     */

    public void inicia() {
        vistaSeleccionEstacion.muestra(this);
    }

    /**
     * Abre un nuevo tablero interactivo de cocina para la estación seleccionada.
     */ 

    public void abrirTableroEstacion(String nombreEstacion){
        Platform.runLater(() -> {
            // 1. Pedimos a Spring que fabrique una vista NUEVA
            VistaCocinaPantalla nuevaVista = context.getBean(VistaCocinaPantalla.class);
            
            // 2. La mostramos y extraemos el controlador de ESA ventana especifica
            CocinaPantallaControlador controladorDeVentana = nuevaVista.muestraYDevuelveControlador();
            
            // 3. Le asignamos el nombre de la estacion y la guardamos en nuestra lista
            controladorDeVentana.setEstacionActual(nombreEstacion);
            ventanasAbiertas.add(controladorDeVentana);

            // 4. Cargamos los pedidos para todas las ventanas
            cargarPedidosPendientes();
        });
    }

    /**
     * Recupera los pedidos pendientes desde la capa de negocio y actualiza de manera reactiva
     * todas las pantallas de cocina abiertas en el sistema
     */

    public void cargarPedidosPendientes() {
        try {
            List<Pedido> pedidosPendientes = servicioPedido.recuperaPedidosPendientes();
            
            if (pedidosPendientes != null) {
                // Actualizamos todas las ventanas que esten abiertas simultaneamente
                for (CocinaPantallaControlador controlador : ventanasAbiertas) {
                    controlador.mostrarPedidosPendientes(pedidosPendientes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}