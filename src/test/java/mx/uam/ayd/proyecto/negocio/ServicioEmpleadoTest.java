package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.EmpleadoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Empleado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ServicioEmpleado.
 * Evalua las reglas de negocio: alta exitosa, usuario duplicado y contrasenia debil.
 * 
 * Cumple con DoD #2 (Pruebas unitarias) y DoD #5 (Documentacion de codigo).
 */
@SuppressWarnings("null")
class ServicioEmpleadoTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private ServicioEmpleado servicioEmpleado;

    private AutoCloseable closeable;

    private String nombreValido;
    private String usuarioValido;
    private String passwordSegura;
    private String rolValido;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        nombreValido = "Juan Perez";
        usuarioValido = "jperez";
        passwordSegura = "ClaveSegura123!";
        rolValido = "Encargado";
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    @DisplayName("Debe registrar exitosamente un empleado cuando los datos y la contrasenia son validos")
    void testRegistrarEmpleadoExitoso() {
        // Arrange
        when(empleadoRepository.existsByUsuario(usuarioValido)).thenReturn(false);
        when(empleadoRepository.save(isA(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Empleado resultado = servicioEmpleado.registrarEmpleado(nombreValido, usuarioValido, passwordSegura, rolValido);

        // Assert
        assertNotNull(resultado, "El empleado registrado no debe ser nulo");
        assertEquals(nombreValido, resultado.getNombre());
        assertEquals(usuarioValido, resultado.getUsuario());
        assertEquals(rolValido, resultado.getRol());
        assertNotEquals(passwordSegura, resultado.getPassword(), "La contrasenia debe estar encriptada");
        verify(empleadoRepository, times(1)).existsByUsuario(usuarioValido);
        verify(empleadoRepository, times(1)).save(isA(Empleado.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el usuario ya existe")
    void testRegistrarEmpleadoUsuarioDuplicado() {
        // Arrange
        when(empleadoRepository.existsByUsuario(usuarioValido)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioEmpleado.registrarEmpleado(nombreValido, usuarioValido, passwordSegura, rolValido),
            "Debe lanzar excepcion si el usuario ya existe"
        );

        assertTrue(exception.getMessage().contains("ya se encuentra registrado"));
        verify(empleadoRepository, times(1)).existsByUsuario(usuarioValido);
        verify(empleadoRepository, never()).save(isA(Empleado.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la contrasenia es debil")
    void testRegistrarEmpleadoPasswordDebil() {
        // Arrange
        String passwordDebil = "12345";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> servicioEmpleado.registrarEmpleado(nombreValido, usuarioValido, passwordDebil, rolValido),
            "Debe lanzar excepcion si la contrasenia es debil"
        );

        assertTrue(exception.getMessage().contains("politicas de seguridad"));
        verify(empleadoRepository, never()).save(isA(Empleado.class));
    }

    @Test
    @DisplayName("Debe validar correctamente la fortaleza de contrasenias validas e invalidas")
    void testValidarPasswordSegura() {
        assertTrue(servicioEmpleado.validarPasswordSegura("Abc1234!"), "Debe aceptar contrasenia valida");
        assertFalse(servicioEmpleado.validarPasswordSegura("12345678"), "Debe rechazar sin mayuscula ni especial");
        assertFalse(servicioEmpleado.validarPasswordSegura("SinNumero!"), "Debe rechazar sin numeros");
        assertFalse(servicioEmpleado.validarPasswordSegura("sinmayus1!"), "Debe rechazar sin mayusculas");
        assertFalse(servicioEmpleado.validarPasswordSegura("SINMINUS1!"), "Debe rechazar sin minusculas");
        assertFalse(servicioEmpleado.validarPasswordSegura(null), "Debe rechazar contrasenia nula");
    }
}
