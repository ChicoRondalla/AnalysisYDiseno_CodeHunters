package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioOrden;
import mx.uam.ayd.proyecto.negocio.ServicioPedido;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.CocinaPantallaControlador;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;

/**
 * Pruebas unitarias para validar el comportamiento del controlador de la pantalla de cocina.
 */
class CocinaPantallaControladorTest {

    @InjectMocks
    private CocinaPantallaControlador cocinaPantallaControlador;

    @Mock
    private ServicioPedido servicioPedido;

    @Mock
    private ServicioOrden servicioOrden;

    // Repositorio falso con @Mock
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ControlVisualizarOrden controlVisualizarOrden;

    private Pedido pedidoPrueba;
    private DetallesPedido detalleRollos;
    private DetallesPedido detallePlancha;

    @BeforeEach
    void setUp() {
        // Inicializamos Mockito para que reconozca los mocks
        MockitoAnnotations.openMocks(this);

        // Datos de prueba comunes
        pedidoPrueba = new Pedido();
        pedidoPrueba.setIdPedido(1L);
        pedidoPrueba.setNumeroOrden(1);
        pedidoPrueba.setEstado("Pendiente");

        Platillo platilloRollos = new Platillo();
        platilloRollos.setNombre("Rollo Mar y Tierra");
        platilloRollos.setTipoArea("ROLLOS");

        Platillo platilloPlancha = new Platillo();
        platilloPlancha.setNombre("Yakimeshi Mixto");
        platilloPlancha.setTipoArea("PLANCHA");

        detalleRollos = new DetallesPedido();
        detalleRollos.setCantidad(1);
        detalleRollos.setPlatillo(platilloRollos);
        detalleRollos.setCompletado(false);

        detallePlancha = new DetallesPedido();
        detallePlancha.setCantidad(1);
        detallePlancha.setPlatillo(platilloPlancha);
        detallePlancha.setCompletado(false);
    }

    // PRUEBAS DE FINALIZACIÓN DE ORDEN DESDE COCINA
    @Test
    void testFinalizarOrden_ExitoYActualizacionDeVista() {
        long idPedido = 1L;
        
        when(servicioPedido.recuperaPedido(idPedido)).thenReturn(pedidoPrueba);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // Ejecutamos la finalización
        cocinaPantallaControlador.finalizarOrden(idPedido);

        // Verificamos cambios de estado y llamadas a repositorios/controladores
        assertEquals("Completado", pedidoPrueba.getEstado(), "El estado del pedido debió cambiar a Completado.");
        verify(servicioPedido, times(1)).recuperaPedido(idPedido);
        verify(pedidoRepository, times(1)).save(pedidoPrueba);
        verify(controlVisualizarOrden, times(1)).cargarPedidosPendientes();
    }

    // PRUEBAS DE FILTRADO DE PLATILLOS POR ESTACIÓN
    @Test
    void testMostrarPedidosPendientes_FiltraCorrectamentePorEstacion() {
        List<DetallesPedido> detalles = Arrays.asList(detalleRollos, detallePlancha);
        when(servicioOrden.obtenerDetallesDePedido(1L)).thenReturn(detalles);

        List<Pedido> listaPedidos = Arrays.asList(pedidoPrueba);

        // Ejecutamos el método que procesa el filtrado visual interno
        cocinaPantallaControlador.mostrarPedidosPendientes(listaPedidos);

        // Verificamos que se consultaron los detalles correspondientes para aplicar el criterio de estación
        verify(servicioOrden, times(1)).obtenerDetallesDePedido(1L);
    }
}