package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad que representa a un empleado dentro del sistema Sushi-Code.
 * Aplica la restriccion de usuario unico en la base de datos.
 */
@Entity
@Table(name = "empleados", uniqueConstraints = {
    @UniqueConstraint(columnNames = "usuario", name = "uk_empleado_usuario")
})
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Empleado() {
    }

    /**
     * Constructor para instanciar un nuevo empleado con todos sus atributos requeridos.
     * 
     * @param nombre   Nombre completo del empleado.
     * @param usuario  Nombre de usuario unico para inicio de sesion.
     * @param password Contrasenia del empleado.
     * @param rol      Rol o perfil asignado dentro del sistema.
     */
    public Empleado(String nombre, String usuario, String password, String rol) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    /**
     * Obtiene el identificador unico del empleado.
     * 
     * @return ID del empleado.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador unico del empleado.
     * 
     * @param id ID a asignar.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del empleado.
     * 
     * @return Nombre completo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del empleado.
     * 
     * @param nombre Nombre completo.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el nombre de usuario.
     * 
     * @return Nombre de usuario.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Establece el nombre de usuario.
     * 
     * @param usuario Nombre de usuario unico.
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene la contrasenia del empleado.
     * 
     * @return Contrasenia del empleado.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contrasenia del empleado.
     * 
     * @param password Contrasenia del empleado.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el rol del empleado.
     * 
     * @return Rol asignado.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del empleado.
     * 
     * @param rol Rol asignado.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
}
