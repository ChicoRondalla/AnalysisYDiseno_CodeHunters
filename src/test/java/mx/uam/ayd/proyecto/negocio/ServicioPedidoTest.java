package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.datos.PedidoRepository;
/**
 * Pruebas unitarias para validar las reglas de negocio de ServicioPedido.
 */
class ServicioPedidoTest {

    @InjectMocks
    private ServicioPedido servicioPedido;

    // repositorio falso con @Mock
    @Mock
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        // Inicializamos Mockito para que reconozca el repositorio
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testValidarDatosDomicilio_Exitoso() {
        // Escenario 1: Pedido a domicilio exitoso (Todos los datos correctos)
        boolean resultado = servicioPedido.validarDatosDomicilio("Hiroshi Tanaka", "5512345678", "Calle 123");
        assertTrue(resultado, "El sistema debería aceptar los datos válidos.");
    }

    @Test
    void testValidarDatosDomicilio_FaltaDireccion() {
        // Escenario 2: Bloqueo por falta de dirección (RN-02)
        boolean resultado = servicioPedido.validarDatosDomicilio("Hiroshi Tanaka", "5512345678", "");
        assertFalse(resultado, "El sistema debería rechazar el pedido si falta la dirección.");
    }

    @Test
    void testValidarDatosDomicilio_TelefonoInvalido() {
        // Escenario 2 variante: Bloqueo porque el teléfono no tiene 10 dígitos (RN-02)
        boolean resultado = servicioPedido.validarDatosDomicilio("Hiroshi Tanaka", "12345", "Calle 123");
        assertFalse(resultado, "El sistema debería rechazar el pedido si el teléfono no tiene 10 dígitos.");
        
        // Bloqueo porque el teléfono tiene letras
        boolean resultadoLetras = servicioPedido.validarDatosDomicilio("Hiroshi Tanaka", "55ABC45678", "Calle 123");
        assertFalse(resultadoLetras, "El sistema debería rechazar el pedido si el teléfono contiene letras.");
    }

    
    // PRUEBAS (HU-03: Envio a Cocina)
    // BLOQUE EL ENVIO SI LA ORDEN FUE CANCELADA
    @Test
    void testProcesarEnvioCocina_FallaCuandoPedidoEstaCancelado() {
        long idPedido = 1L;
        // PEDIDO SIMULADO
        Pedido pedidoCancelado = new Pedido();
        pedidoCancelado.setEstado("Cancelada");
        // SIMULAMOS RESPUESTA DE MOCKITO
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedidoCancelado));
        // ESPERAMOS QUE FALLE
        Exception excepcion = assertThrows(IllegalStateException.class, () -> {
            servicioPedido.procesarEnvioCocina(idPedido);
        });
        // VERIFICA EL MENSAJE 
        assertEquals("Esta orden ha sido cancelada y no puede enviarse a cocina.", excepcion.getMessage());
    }
    
    // VERIFICA EL CAMINO FELIZ
    @Test
    void testProcesarEnvioCocina_ExitoYCambioDeEstado() {
        long idPedido = 2L;
        //PEDIDO VALIDO
        Pedido pedidoValido = new Pedido();
        pedidoValido.setEstado("Pendiente"); 

        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedidoValido));
        // ENVIO A COSINA
        boolean resultado = servicioPedido.procesarEnvioCocina(idPedido);
        // VERIFICA QUE TODO ESTE BIEN
        assertTrue(resultado, "El método debe devolver true si el envío fue exitoso.");
        assertEquals("En Preparación", pedidoValido.getEstado(), "El estado del pedido debió cambiar.");
        // VERIFICAMOS QUE SE GUARDE EN LA BD
        verify(pedidoRepository, times(1)).save(pedidoValido);
    }

    // PRUEBAS (HU-04: Cancela un Pedido)
    // PRUEBA JUSTIFICACION OBLIGATORIA 
    @Test
    void testCancelarPedido_FallaCuandoMotivoEstaVacio() {
        long idPedido = 1L;
        String motivoVacio = "   "; // MOTIVO INVALIDO
        String idUsuario = "Cajero01";
        // LANZA EL ERROR 
        Exception excepcion = assertThrows(IllegalArgumentException.class, () -> {
            servicioPedido.cancelarPedido(idPedido, motivoVacio, idUsuario);
        });

        assertEquals("El motivo de cancelación es obligatorio.", excepcion.getMessage());
    }

    // ASEGURA QUE NO CANCELE ALGO QUE NO EXISTE 
    @Test
    void testCancelarPedido_FallaCuandoPedidoNoExiste() {
        long idFalso = 99L;
        // SIMULA QUE NO ENCUENTRA EL PEDIDO
        when(pedidoRepository.findById(idFalso)).thenReturn(Optional.empty());

        Exception excepcion = assertThrows(IllegalArgumentException.class, () -> {
            servicioPedido.cancelarPedido(idFalso, "Ya no lo quiere", "Cajero01");
        });

        assertEquals("No se encontró el pedido con ID: " + idFalso, excepcion.getMessage());
    }

    // VRIFICA QUE EL ESTADO CAMBIE Y GUARDE EL FORMATO
    @Test
    void testCancelarPedido_ExitoYGuardaMotivoCorrectamente() {
        long idPedido = 2L;
        //PEDIDO VALIDO
        Pedido pedidoValido = new Pedido();
        pedidoValido.setIdPedido(idPedido);
        pedidoValido.setEstado("Pendiente"); 
        // EJECUTA LA CANCELACION
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedidoValido));

        boolean resultado = servicioPedido.cancelarPedido(idPedido, "Cliente se arrepintió", "Admin123");
        // VERIFICA EL ESTADO
        assertTrue(resultado, "El método debe devolver true tras cancelar con éxito.");
        assertEquals("Cancelada", pedidoValido.getEstado(), "El estado debió cambiar a Cancelada.");
        // VERIFICA QUE ESTA EL MOTIVO Y USUARIO
        assertEquals("Cliente se arrepintió (Cancelado por: Admin123)", pedidoValido.getMotivoCancelacion(), 
            "El motivo de cancelación no se guardó con el formato esperado.");
        verify(pedidoRepository, times(1)).save(pedidoValido);
    }

}

