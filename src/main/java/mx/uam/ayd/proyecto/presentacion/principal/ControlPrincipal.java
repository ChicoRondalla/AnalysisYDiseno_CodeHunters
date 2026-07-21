package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.agregarUsuario.ControlAgregarUsuario;
import mx.uam.ayd.proyecto.presentacion.listarUsuarios.ControlListarUsuarios;
import mx.uam.ayd.proyecto.presentacion.principal.VentanaPrincipal;
import mx.uam.ayd.proyecto.presentacion.listarGrupos.ControlListarGrupos;

import mx.uam.ayd.proyecto.presentacion.registrarPedido.ControlRegistroPedido;
import mx.uam.ayd.proyecto.presentacion.registrarPedido.VistaRegistroPedido;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Esta clase lleva el flujo de control de la ventana principal
 * 
 * @author humbertocervantes
 *
 */
@Component
public class ControlPrincipal {

	private final ControlAgregarUsuario controlAgregarUsuario;
	private final ControlListarUsuarios controlListarUsuarios;
	private final ControlListarGrupos controlListarGrupos;
	private final VentanaPrincipal ventana;
	
	@Autowired
	public ControlPrincipal(
			ControlAgregarUsuario controlAgregarUsuario,
			ControlListarUsuarios controlListarUsuarios,
			ControlListarGrupos controlListarGrupos,
			VentanaPrincipal ventana) {
		this.controlAgregarUsuario = controlAgregarUsuario;
		this.controlListarUsuarios = controlListarUsuarios;
		this.controlListarGrupos = controlListarGrupos;
		this.ventana = ventana;
	}
	
	@Autowired
    private VistaRegistroPedido vistaRegistroPedido;

    @Autowired
    private ControlRegistroPedido controlRegistroPedido;

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

	/**
	 * Método que arranca la historia de usuario "agregar usuario"
	 * 
	 */
	public void agregarUsuario() {
		controlAgregarUsuario.inicia();
	}
	
	/**
	 * Método que arranca la historia de usuario "listar usuarios"
	 * 
	 */
	public void listarUsuarios() {
		controlListarUsuarios.inicia();
	}

	/**
	 * Método que arranca la historia de usuario "listar grupos"
	 * 
	 */
	public void listarGrupos() {
		controlListarGrupos.inicia();
	}

	
}
