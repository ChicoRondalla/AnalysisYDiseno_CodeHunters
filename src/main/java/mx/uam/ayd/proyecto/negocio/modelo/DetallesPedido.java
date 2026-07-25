package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio DetallesPedido
 */
@Entity
public class DetallesPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDetallePedido;

    private int cantidad;
    private int subtotal;
    private String notas;


    // --- RELACIONES DEL MODELO DE DOMINIO ---
    // Muchos DetallesPedido pertenecen a 1 Pedido
    @ManyToOne(targetEntity = Pedido.class, fetch = FetchType.LAZY)
    private Pedido pedido;

    // 1 DetallePedido CORRESPONDE a 1 Platillo
    @ManyToOne(targetEntity = Platillo.class, fetch = FetchType.EAGER)
    private Platillo platillo;


    // --- GETTERS Y SETTERS ---
    public long getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(long idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    
    
    // LO AGREGE PARA LA HU-03
    public Platillo getPlatillo() {
        return platillo;
    }

    public void setPlatillo(Platillo platillo) {
        this.platillo = platillo;
    }
}
