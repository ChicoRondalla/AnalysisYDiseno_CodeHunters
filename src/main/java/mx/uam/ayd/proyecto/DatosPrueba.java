package mx.uam.ayd.proyecto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.datos.PlatilloRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;

/**
 * Clase encargada de poblar la base de datos H2 en memoria con datos de prueba
 * cada vez que arranca la aplicación.
 */
@Component
public class DatosPrueba implements CommandLineRunner {

    @Autowired
    private PlatilloRepository platilloRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Solo insertamos si la tabla está vacía
        if (platilloRepository.count() == 0) {
            System.out.println("Iniciando carga de platillos de prueba en H2...");

            Platillo p1 = new Platillo();
            p1.setNombre("Rollo Mar y Tierra");
            p1.setPrecio(120);
            p1.setTipoArea("Rollos");
            p1.setDescripcion("Rollo empanizado con camarón, res y aguacate.");
            platilloRepository.save(p1);

            Platillo p2 = new Platillo();
            p2.setNombre("Nigiri de Salmón");
            p2.setPrecio(85);
            p2.setTipoArea("Rollos");
            p2.setDescripcion("Dos piezas tradicionales de salmón fresco.");
            platilloRepository.save(p2);

            Platillo p3 = new Platillo();
            p3.setNombre("Yakimeshi Mixto");
            p3.setPrecio(150);
            p3.setTipoArea("Plancha");
            p3.setDescripcion("Arroz frito con pollo, res y vegetales.");
            platilloRepository.save(p3);
            
            Platillo p4 = new Platillo();
            p4.setNombre("Limonada Mineral");
            p4.setPrecio(45);
            p4.setTipoArea("Bebidas");
            p4.setDescripcion("Limonada fresca con agua mineral.");
            platilloRepository.save(p4);

            System.out.println("¡Carga de platillos completada!");
        }
    }
}
