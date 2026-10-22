package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.MesaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioMesa {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> obtenerMesas() {
        return (List<Mesa>) mesaRepository.findAll();
    }

    public Optional<Mesa> obtenerMesaPorNumero(int numeroMesa) {
        return mesaRepository.findByNumeroMesa(numeroMesa);
    }

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