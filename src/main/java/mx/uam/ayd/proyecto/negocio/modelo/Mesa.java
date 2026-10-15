package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa una mesa física dentro del restaurante para la HU-05[cite: 5].
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numeroMesa;
    private int capacidad;
    
    // Estado de la mesa: "LIBRE" u "OCUPADA"[cite: 5]
    private String estado;

    /**
     * Constructor conveniente para inicializar mesas sin ID (autogenerado).
     */
    public Mesa(int numeroMesa, int capacidad, String estado) {
        this.numeroMesa = numeroMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    /**
     * Verifica si la mesa se encuentra disponible[cite: 5].
     */
    public boolean isLibre() {
        return "LIBRE".equalsIgnoreCase(this.estado);
    }
}