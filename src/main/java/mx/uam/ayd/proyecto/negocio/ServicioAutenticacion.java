package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación del administrador (HU-03, Escenario 0).
 */
@Service
public class ServicioAutenticacion {

    public static final String ROL_ADMIN = "Administrador";

    private final EmpleadoRepository empleadoRepository;
    private volatile boolean adminAutenticado = false;

    @Autowired
    public ServicioAutenticacion(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Valida usuario, contraseña y que el rol sea Administrador.
     * @return true si las credenciales son válidas y el usuario es administrador.
     */
    public boolean autenticarAdministrador(String usuario, String contrasena) {
        boolean ok = empleadoRepository.findByUsuario(usuario)
                .filter(e -> ROL_ADMIN.equalsIgnoreCase(e.getRol()))
                // OJO: si ServicioEmpleado guarda la contraseña hasheada, cambia esta comparación
                .filter(e -> e.getPassword().equals(contrasena))
                .isPresent();
        adminAutenticado = ok;
        return ok;
    }

    public boolean isAdminAutenticado() {
        return adminAutenticado;
    }

    public void cerrarSesion() {
        adminAutenticado = false;
    }
}