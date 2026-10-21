package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import mx.uam.ayd.proyecto.negocio.modelo.Mesa;

/**
 * Pruebas de integración para verificar la persistencia y el cambio de estado de las mesas en la base de datos real.
 */
@SpringBootTest
public class ServicioMesaIntegrationTest {

    @Autowired
    private ServicioMesa servicioMesa;

    @Test
    void testCambioEstadoMesaEnBaseDeDatosReal() {
        int numeroMesaPrueba = 1;

        // 1. Asegurar que la mesa comience libre
        servicioMesa.actualizarEstadoMesa(numeroMesaPrueba, "LIBRE");
        
        Optional<Mesa> mesaAntes = servicioMesa.obtenerMesaPorNumero(numeroMesaPrueba);
        assertTrue(mesaAntes.isPresent(), "La mesa de prueba debe existir en la base de datos");
        assertEquals("LIBRE", mesaAntes.get().getEstado(), "El estado inicial debe ser LIBRE");

        // 2. Ejecutar la actualización de estado a OCUPADA
        servicioMesa.actualizarEstadoMesa(numeroMesaPrueba, "OCUPADA");

        // 3. Verificar que el cambio se reflejó consultando directamente de la BD
        Optional<Mesa> mesaDespues = servicioMesa.obtenerMesaPorNumero(numeroMesaPrueba);
        assertTrue(mesaDespues.isPresent());
        assertEquals("OCUPADA", mesaDespues.get().getEstado(), "El estado en la base de datos debió cambiar a OCUPADA");

        // Limpieza posterior: Devolver la mesa a su estado LIBRE para no alterar otras pruebas
        servicioMesa.actualizarEstadoMesa(numeroMesaPrueba, "LIBRE");
    }
}