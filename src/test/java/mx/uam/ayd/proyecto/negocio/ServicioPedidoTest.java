package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para validar las reglas de negocio de ServicioPedido.
 */
class ServicioPedidoTest {

    private ServicioPedido servicioPedido;

    @BeforeEach
    void setUp() {
        servicioPedido = new ServicioPedido();
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
}
