package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


// 1. CONTROLADOR DE LA HISTORIA DE USUARIO HU-03
import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;
import mx.uam.ayd.proyecto.presentacion.registrarPedido.ControlRegistroPedido;
import mx.uam.ayd.proyecto.presentacion.registrarPedido.VistaRegistroPedido;



@Component
public class ControlPrincipal {


	@Autowired
	private VentanaPrincipal ventana;	
	// HU-O1	
	@Autowired
    private VistaRegistroPedido vistaRegistroPedido;

    @Autowired
    private ControlRegistroPedido controlRegistroPedido;

    // HU-03
    @Autowired
    private ControlEnviarOrdenCocina controlEnviarOrdenCocina;

    /**
     * Método para abrir la historia de usuario HU-01
     */
    public void registrarPedido() {
        vistaRegistroPedido.muestra(controlRegistroPedido);
    }


	/**
	 * Método que se ejecuta después de la construcción del bean
	 * y realiza la conexión bidireccional entre el control principal y la ventana principal
	 */
	@PostConstruct
	public void init() {
		ventana.setControlPrincipal(this);
	}
	
	/**
	 * Inicia el flujo de control de la ventana principal
	 * 
	 */
	public void inicia() {
		//Comentamos esta parte para que no se abra la ventana que tenia de 'Mi Aplicacion' profesor, una disculpa
		//ventana.muestra();

		//Para que incie directamente con el menu de la HU-01 para seleccionar el tipo de pedido
		registrarPedido();
	}

    // 5. MÉTODO QUE ARRANCA LA HISTORIA DE USUARIO "ENVIAR ORDEN A COCINA" (HU-03)
        
    public void enviarOrdenCocina() {
        // ID DE PEDIDO SIMULADO PARA PRUEBAS
        controlEnviarOrdenCocina.inicia(1L); 
    }
}



