package mx.uam.ayd.proyecto.negocio;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la lógica de negocio para los pedidos.
 */
@Service
public class ServicioPedido {

    /**
     * Valida los datos de un pedido a domicilio según la RN-02.
     * 
     * @param nombre El nombre del cliente.
     * @param telefono El teléfono del cliente (debe tener exactamente 10 dígitos).
     * @param direccion La dirección de entrega (no puede estar vacía).
     * @return true si los datos son válidos, false si rompen alguna regla de negocio.
     */
    public boolean validarDatosDomicilio(String nombre, String telefono, String direccion) {
        // Validación de campos vacíos
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (direccion == null || direccion.trim().isEmpty()) return false;
        if (telefono == null || telefono.trim().isEmpty()) return false;

        // Validación estricta de 10 dígitos numéricos para el teléfono
        if (!telefono.matches("\\d{10}")) {
            return false;
        }

        // Si pasa todas las validaciones
        return true;
    }
}
