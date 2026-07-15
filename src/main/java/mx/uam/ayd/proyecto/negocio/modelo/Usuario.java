package mx.uam.ayd.proyecto.negocio.modelo;

//Entidad de negocio que representa a un Usuario en Ryuho Sushi.
public class Usuario {
    private String idUsuario;
    private String rol;
    public String nombre; 

    public Usuario() {
    }

    public Usuario(String idUsuario, String rol, String nombre) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.nombre = nombre;
    }

    public String getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}