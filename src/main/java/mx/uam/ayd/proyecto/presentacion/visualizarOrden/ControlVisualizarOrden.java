package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;

@Component
public class ControlVisualizarOrden {

    @Autowired private VistaSeleccionEstacion vistaSeleccionEstacion;
    @Autowired private ServicioPedido servicioPedido;
    
    // El contexto nos sirve como "fábrica" para crear ventanas nuevas
    @Autowired private ApplicationContext context; 

    // Lista para guardar todas las ventanas que el usuario abra
    private final List<CocinaPantallaControlador> ventanasAbiertas = new ArrayList<>();

    public void inicia() {
        vistaSeleccionEstacion.muestra(this);
    }

    public void abrirTableroEstacion(String nombreEstacion){
        Platform.runLater(() -> {
            // 1. Pedimos a Spring que fabrique una vista NUEVA
            VistaCocinaPantalla nuevaVista = context.getBean(VistaCocinaPantalla.class);
            
            // 2. La mostramos y extraemos el controlador de ESA ventana específica
            CocinaPantallaControlador controladorDeVentana = nuevaVista.muestraYDevuelveControlador();
            
            // 3. Le asignamos el nombre de la estación y la guardamos en nuestra lista
            controladorDeVentana.setEstacionActual(nombreEstacion);
            ventanasAbiertas.add(controladorDeVentana);

            // 4. Cargamos los pedidos para todas las ventanas
            cargarPedidosPendientes();
        });
    }

    public void cargarPedidosPendientes() {
        try {
            List<Pedido> pedidosPendientes = servicioPedido.recuperaPedidosPendientes();
            
            if (pedidosPendientes != null) {
                // Actualizamos TODAS las ventanas que estén abiertas simultáneamente
                for (CocinaPantallaControlador controlador : ventanasAbiertas) {
                    controlador.mostrarPedidosPendientes(pedidosPendientes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}