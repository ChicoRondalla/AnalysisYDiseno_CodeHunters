package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 1. IMPORTAMOS EL CONTROLADOR DE LA HISTORIA DE USUARIO HU-03
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;

/**
 * LLEVA EL CONTROL DE LA VENTANA PRINCIPAL Y ARRANCA LA HISTORIA DE USUARIO HU-03
 */
@Component
public class ControlPrincipal {

    // 2. DECLARAMOS EL CONTROLADOR DE LA HISTORIA DE USUARIO HU-03 COMO UNA DEPENDENCIA
    private final ControlEnviarOrdenCocina controlEnviarOrdenCocina; 
    private final VentanaPrincipal ventana;
    
    @Autowired
    public ControlPrincipal(
            // 3. LO INYECTAMOS EN EL CONSTRUCTOR
            ControlEnviarOrdenCocina controlEnviarOrdenCocina, 
            VentanaPrincipal ventana) {
        
        // 4. LO ASIGNAMOS 
        this.controlEnviarOrdenCocina = controlEnviarOrdenCocina; 
        this.ventana = ventana;
    }
    
    /**
     * METODO QUE CONECTA EL CONTROLADOR DE LA HISTORIA DE USUARIO HU-03 CON EL BOTON DE LA VENTANA PRINCIPAL
     */
    @PostConstruct
    public void init() {
        ventana.setControlPrincipal(this);
    }
    
    /**
     * INICIA EL CONTROL PRINCIPAL Y MUESTRA LA VENTANA PRINCIPAL
     */
    public void inicia() {
        ventana.muestra();
    }

    // ---------------------------------------------------------
    // 5. MÉTODO QUE ARRANCA LA HISTORIA DE USUARIO "ENVIAR ORDEN A COCINA" (HU-03)
    // ---------------------------------------------------------
    
    public void enviarOrdenCocina() {
        // ID DE PEDIDO SIMULADO PARA PRUEBAS
        controlEnviarOrdenCocina.inicia(1L); 
    }
}