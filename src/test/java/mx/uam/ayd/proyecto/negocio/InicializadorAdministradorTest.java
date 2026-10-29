package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import mx.uam.ayd.proyecto.datos.EmpleadoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Empleado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de {@link InicializadorAdministrador}.
 *
 * <p>Como la base H2 vive en memoria y se borra en cada ejecución, esta clase
 * es la que garantiza que siempre exista al menos un administrador para poder
 * entrar al módulo de personal. Probamos las dos ramas: crearlo cuando falta y
 * no duplicarlo cuando ya existe (el usuario es {@code UNIQUE}, así que un
 * segundo {@code save} reventaría el arranque de la aplicación).</p>
 */
@ExtendWith(MockitoExtension.class)
class InicializadorAdministradorTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private InicializadorAdministrador inicializador;

    @Test
    @DisplayName("Si no existe el usuario admin, lo crea con rol Administrador")
    void creaAdministradorCuandoNoExiste() {
        when(empleadoRepository.existsByUsuario("admin")).thenReturn(false);

        inicializador.run(null); // los argumentos de arranque no se usan

        ArgumentCaptor<Empleado> captor = ArgumentCaptor.forClass(Empleado.class);
        verify(empleadoRepository, times(1)).save(captor.capture());

        Empleado guardado = captor.getValue();
        assertEquals("admin", guardado.getUsuario());
        assertEquals(ServicioAutenticacion.ROL_ADMIN, guardado.getRol());
    }

    @Test
    @DisplayName("Si el usuario admin ya existe, no guarda nada (evita duplicados)")
    void noDuplicaAdministradorSiYaExiste() {
        when(empleadoRepository.existsByUsuario("admin")).thenReturn(true);

        inicializador.run(null);

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }
}