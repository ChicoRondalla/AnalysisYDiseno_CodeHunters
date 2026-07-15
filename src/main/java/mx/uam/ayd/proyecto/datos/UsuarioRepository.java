package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import java.util.Optional;
//Repositorio para gestionar el acceso a datos de los usuarios(Empleados).
public class UsuarioRepository {


     //Recupera un usuario de la persistencia por su ID.
    public Optional<Usuario> findById(String idUsuario) {
        // Mock de prueba rápido:
        if ("EMP01".equals(idUsuario)) {
            return Optional.of(new Usuario("EMP01", "RECEPCIONISTAS", "Josué"));
        } else if ("EMP02".equals(idUsuario)) {
            return Optional.of(new Usuario("EMP02", "CHEF", "Chef Richard"));
        }
        return Optional.empty();
    }
}