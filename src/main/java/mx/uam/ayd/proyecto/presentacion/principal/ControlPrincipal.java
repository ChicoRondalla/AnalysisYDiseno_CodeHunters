package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.principal.VentanaPrincipal;

import mx.uam.ayd.proyecto.presentacion.enviarOrdenCocina.ControlEnviarOrdenCocina;

import mx.uam.ayd.proyecto.presentacion.registrarPedido.ControlRegistroPedido;
import mx.uam.ayd.proyecto.presentacion.registrarPedido.VistaRegistroPedido;



@Component
public class ControlPrincipal {

	private final VentanaPrincipal ventana;
	
	@Autowired
	public ControlPrincipal(
			VentanaPrincipal ventana) {
		this.ventana = ventana;
	}
	
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
		//Para que incie directamente con el menu de la HU-01 para seleccionar el tipo de pedido
		registrarPedido();
	}

    // 5. MÉTODO QUE ARRANCA LA HISTORIA DE USUARIO "ENVIAR ORDEN A COCINA" (HU-03)
        
    public void enviarOrdenCocina() {
        // ID DE PEDIDO SIMULADO PARA PRUEBAS
        controlEnviarOrdenCocina.inicia(1L); 
    }

}