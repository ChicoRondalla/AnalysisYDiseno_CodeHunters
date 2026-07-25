package mx.uam.ayd.proyecto.presentacion.visualizarOrden;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Control del caso de uso: Visualizar Orden / Comanda en Cocina
 */
@Component
public class ControlVisualizarOrden {

    private final VistaCocinaPantalla vistaCocinaPantalla;

    @Autowired
    public ControlVisualizarOrden(VistaCocinaPantalla vistaCocinaPantalla) {
        this.vistaCocinaPantalla = vistaCocinaPantalla;
    }

    /**
     * Inicia la ventana de cocina
     */
    public void inicia() {
        vistaCocinaPantalla.muestra(this);
    }
}