package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import mx.uam.ayd.proyecto.datos.EmpleadoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Empleado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de {@link ServicioAutenticacion} (HU-03, Escenario 0).
 *
 * <p>Usamos Mockito para "simular" el repositorio. Así las pruebas no
 * necesitan levantar Spring ni la base H2: corren en milisegundos y solo
 * verifican la lógica del servicio, no la base de datos. Cada prueba sigue el
 * patrón Dado/Cuando/Entonces (Given/When/Then), igual que los criterios de
 * aceptación.</p>
 *
 * <p>Con {@code MockitoExtension} el modo es estricto: si una prueba define un
 * "stub" que nunca se usa, falla con {@code UnnecessaryStubbingException}.
 * Está bien, nos obliga a mantener las pruebas limpias.</p>
 */
@ExtendWith(MockitoExtension.class)
class ServicioAutenticacionTest {

    private static final String USUARIO = "admin";
    private static final String PASSWORD = "Admin123!";

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private ServicioAutenticacion servicio;

    /** Construye un empleado de prueba con el rol indicado. */
    private Empleado empleado(String usuario, String password, String rol) {
        return new Empleado("Nombre de Prueba", usuario, password, rol);
    }

    @Test
    @DisplayName("Al iniciar, no hay ninguna sesión de administrador activa")
    void sesionInicialNoEstaAutenticada() {
        assertFalse(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("Credenciales correctas de un administrador: devuelve true y activa la sesión")
    void autenticarAdministradorConCredencialesCorrectas() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "Administrador")));

        boolean resultado = servicio.autenticarAdministrador(USUARIO, PASSWORD);

        assertTrue(resultado);
        assertTrue(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("El rol se compara sin importar mayúsculas/minúsculas")
    void autenticarAdministradorIgnoraMayusculasEnElRol() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "ADMINISTRADOR")));

        assertTrue(servicio.autenticarAdministrador(USUARIO, PASSWORD));
    }

    @Test
    @DisplayName("Usuario inexistente: devuelve false y no activa la sesión")
    void autenticarAdministradorConUsuarioInexistente() {
        when(empleadoRepository.findByUsuario("fantasma")).thenReturn(Optional.empty());

        boolean resultado = servicio.autenticarAdministrador("fantasma", PASSWORD);

        assertFalse(resultado);
        assertFalse(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("Contraseña incorrecta: devuelve false aunque el usuario sea administrador")
    void autenticarAdministradorConContrasenaIncorrecta() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "Administrador")));

        boolean resultado = servicio.autenticarAdministrador(USUARIO, "OtraClave1!");

        assertFalse(resultado);
        assertFalse(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("Contraseña nula: devuelve false y no lanza excepción")
    void autenticarAdministradorConContrasenaNula() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "Administrador")));

        assertFalse(servicio.autenticarAdministrador(USUARIO, null));
    }

    @Test
    @DisplayName("Empleado válido pero sin rol de Administrador: acceso denegado")
    void autenticarAdministradorConEmpleadoQueNoEsAdministrador() {
        // Este es el caso clave del Escenario 0: un cocinero con usuario y contraseña
        // correctos NO debe poder entrar al módulo de personal.
        when(empleadoRepository.findByUsuario("cocinero1"))
                .thenReturn(Optional.of(empleado("cocinero1", "Cocina123!", "Cocinero")));

        boolean resultado = servicio.autenticarAdministrador("cocinero1", "Cocina123!");

        assertFalse(resultado);
        assertFalse(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("Un intento fallido posterior revoca una sesión que estaba activa")
    void intentoFallidoRevocaLaSesionActiva() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "Administrador")));
        when(empleadoRepository.findByUsuario("intruso")).thenReturn(Optional.empty());

        servicio.autenticarAdministrador(USUARIO, PASSWORD);
        assertTrue(servicio.isAdminAutenticado());

        // Cada intento reemplaza el estado de la sesión: si el siguiente falla, queda cerrada.
        servicio.autenticarAdministrador("intruso", "lo-que-sea");
        assertFalse(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("cerrarSesion() deja la sesión sin autenticar")
    void cerrarSesionDesactivaLaSesion() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "Administrador")));
        servicio.autenticarAdministrador(USUARIO, PASSWORD);
        assertTrue(servicio.isAdminAutenticado());

        servicio.cerrarSesion();

        assertFalse(servicio.isAdminAutenticado());
    }

    @Test
    @DisplayName("Autenticar solo consulta datos: nunca guarda nada en el repositorio")
    void autenticarNoModificaElRepositorio() {
        when(empleadoRepository.findByUsuario(USUARIO))
                .thenReturn(Optional.of(empleado(USUARIO, PASSWORD, "Administrador")));

        servicio.autenticarAdministrador(USUARIO, PASSWORD);

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    @DisplayName("La constante del rol coincide con el texto que usan la tabla y el combo")
    void constanteRolAdministradorEsCorrecta() {
        // Si alguien cambia esta constante por error, el login dejaría de reconocer
        // al administrador sin que nada más avise. Esta prueba lo detecta.
        assertEquals("Administrador", ServicioAutenticacion.ROL_ADMIN);
    }
}