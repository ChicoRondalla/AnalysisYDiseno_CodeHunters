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
}
