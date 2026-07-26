package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio DetallesPedido
 * 
 * @author CodeHunters 
 */
@Entity
public class DetallesPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDetallesPedido;

    private int cantidad;
    private String notas;
    private int subtotal;
    private boolean completado = false; // Campo para control de HU-06

    @ManyToOne(targetEntity = Platillo.class)
    private Platillo platillo;

    @ManyToOne(targetEntity = Pedido.class)
    private Pedido pedido;

    // --- GETTERS Y SETTERS ---

    public long getIdDetallesPedido() {
        return idDetallesPedido;
    }

    public long getIdDetallePedido() { // Método alias para compatibilidad
        return idDetallesPedido;
    }

    public void setIdDetallesPedido(long idDetallesPedido) {
        this.idDetallesPedido = idDetallesPedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public Platillo getPlatillo() {
        return platillo;
    }

    public void setPlatillo(Platillo platillo) {
        this.platillo = platillo;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}