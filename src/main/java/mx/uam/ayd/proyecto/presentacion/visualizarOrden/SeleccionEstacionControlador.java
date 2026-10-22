package mx.uam.ayd.proyecto.presentacion.visualizarOrden;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;

import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import  javafx.fxml.FXML;

/**
* Controlador de presentacion para la pantalla de seleccion de estacion
*/

@Component 
public class SeleccionEstacionControlador {

    /**
    * Controlador coordinador encargado de manejar el flujo de visualizaciOn de Ordenes
    * y la apertura de las vistas de cocina correspondientes
    */

    @Autowired
    private ControlVisualizarOrden controlVisualizarOrden;

    /**
    * Maneja el evento de seleccion de la estacion de Rollos
    */

    @FXML
    private void seleccionarRollos() {
        controlVisualizarOrden.abrirTableroEstacion("ROLLOS");
    }

    /**
    * Maneja el evento de seleccion de la estacion de Postres
    */

    @FXML
    private void seleccionarPostres() {
        controlVisualizarOrden.abrirTableroEstacion("POSTRES");
    }

    /**
    * Maneja el evento de seleccion de la estacion de Bebidas
    */

    @FXML
    private void seleccionarBebidas() {
        controlVisualizarOrden.abrirTableroEstacion("BEBIDAS");
    }

    /**
    * Maneja el evento de seleccion de la estacion de Plancha
    */

    @FXML
    private void seleccionarPlancha() {
        controlVisualizarOrden.abrirTableroEstacion("PLANCHA");
    }
}