package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.MesaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio encargado de la gestión, consulta y actualización
 * de los estados y propiedades de las mesas del restaurante (HU-05).
 */
@Service
public class ServicioMesa {

    @Autowired
    private MesaRepository mesaRepository;

    /**
     * Obtiene la lista completa de mesas registradas en el sistema.
     * 
     * @return Lista de objetos Mesa con su respectiva información y estado actual.
     */
    public List<Mesa> obtenerMesas() {
        return (List<Mesa>) mesaRepository.findAll();
    }

    /**
     * Busca y retorna una mesa específica utilizando su número identificador.
     * 
     * @param numeroMesa Número de la mesa que se desea consultar.
     * @return Optional que contiene la mesa si es encontrada, o vacío en caso contrario.
     */
    public Optional<Mesa> obtenerMesaPorNumero(int numeroMesa) {
        return mesaRepository.findByNumeroMesa(numeroMesa);
    }

    /**
     * Actualiza el estado de una mesa (por ejemplo: LIBRE, OCUPADA, RESERVADA)
     * y persiste los cambios directamente en la base de datos.
     * 
     * @param numeroMesa Número identificador de la mesa a actualizar.
     * @param nuevoEstado Cadena de texto con el nuevo estado a asignar.
     * @return La entidad Mesa actualizada y guardada exitosamente.
     * @throws RuntimeException si no se encuentra ninguna mesa con el número proporcionado.
     */
    public Mesa actualizarEstadoMesa(int numeroMesa, String nuevoEstado) {
        Optional<Mesa> mesaOpt = mesaRepository.findByNumeroMesa(numeroMesa);
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();
            mesa.setEstado(nuevoEstado);
            return mesaRepository.save(mesa);
        }
        throw new RuntimeException("Mesa no encontrada con número: " + numeroMesa);
    }
}