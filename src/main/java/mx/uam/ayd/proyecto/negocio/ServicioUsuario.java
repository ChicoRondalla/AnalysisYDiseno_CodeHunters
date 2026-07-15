package mx.uam.ayd.proyecto.negocio;

import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioUsuario {
    
    /**
     * Método para agregar un usuario desde la interfaz gráfica.
     */
    public Usuario agregaUsuario(String idUsuario, String rol, String nombre) {
        return null; 
    }

    /**
     * Método para recuperar la lista de usuarios para la interfaz gráfica.
     */
    public List<Usuario> recuperaUsuarios() {
        return new ArrayList<>(); 
    }

    /**
     * Valida si un usuario existe en el sistema a partir de su ID.
     * @param idUsuario El ID ingresado en el login
     * @return El objeto Usuario si existe, o null si no existe.
     */
    public Usuario validaUsuario(String idUsuario) {
        // TODO: Sustituir por la búsqueda real en UsuarioRepository
        // Mock temporal para probar la interfaz gráfica:
        if ("EMP01".equals(idUsuario)) {
            Usuario u = new Usuario();
            u.setIdUsuario("EMP01");
            u.nombre = "Josué";
            u.setRol("RECEPCIONISTAS");
            return u;
        }
        return null;
    }
}
