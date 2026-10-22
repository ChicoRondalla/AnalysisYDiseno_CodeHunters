package mx.uam.ayd.proyecto.presentacion.visualizarOrden;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;

import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import  javafx.fxml.FXML;

@Component 
public class SeleccionEstacionControlador {

    @Autowired
    private ControlVisualizarOrden controlVisualizarOrden;

    @FXML
    private void seleccionarRollos() {
        controlVisualizarOrden.abrirTableroEstacion("ROLLOS");
    }

    @FXML
    private void seleccionarPostres() {
        controlVisualizarOrden.abrirTableroEstacion("POSTRES");
    }

    @FXML
    private void seleccionarBebidas() {
        controlVisualizarOrden.abrirTableroEstacion("BEBIDAS");
    }

    @FXML
    private void seleccionarPlancha() {
        controlVisualizarOrden.abrirTableroEstacion("PLANCHA");
    }
}