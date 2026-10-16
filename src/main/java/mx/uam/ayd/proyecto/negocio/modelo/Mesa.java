package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad que representa una mesa física dentro del restaurante para la HU-05.
 */
@Entity
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numeroMesa;
    private int capacidad;
    
    // Estado de la mesa: "LIBRE" u "OCUPADA"
    private String estado;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Mesa() {
    }

    /**
     * Constructor conveniente para inicializar mesas sin ID (autogenerado).
     */
    public Mesa(int numeroMesa, int capacidad, String estado) {
        this.numeroMesa = numeroMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Getters y Setters explícitos para evitar conflictos con Java 24
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Verifica si la mesa se encuentra disponible.
     */
    public boolean isLibre() {
        return "LIBRE".equalsIgnoreCase(this.estado);
    }
}