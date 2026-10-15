package mx.uam.ayd.proyecto.presentacion.registrarMesa;

import mx.uam.ayd.proyecto.negocio.ServicioMesa;
import mx.uam.ayd.proyecto.negocio.modelo.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar la selección de mesas y su estado (HU-05).
 */
@Component
public class ControlMesa {

    @Autowired
    private ServicioMesa servicioMesa;

    public void inicia() {
        List<Mesa> mesas = servicioMesa.obtenerMesas();
        // Lógica para mostrar la interfaz gráfica de mesas
    }

    /**
     * Gestiona la selección de una mesa asegurando que esté libre (Escenario 2 y 3 de HU-05).
     */
    public boolean seleccionarMesa(int numeroMesa) {
        Optional<Mesa> mesaOpt = servicioMesa.obtenerMesaPorNumero(numeroMesa);
        
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();
            
            // Si la mesa está ocupada, se bloquea la selección
            if (!mesa.isLibre()) {
                return false; 
            }
            
            // Si está libre, cambia automáticamente a ocupada
            servicioMesa.actualizarEstadoMesa(numeroMesa, "OCUPADA");
            return true;
        }
        return false;
    }
}