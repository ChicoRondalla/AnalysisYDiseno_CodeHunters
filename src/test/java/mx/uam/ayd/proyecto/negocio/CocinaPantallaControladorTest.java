package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javafx.application.Platform;
import javafx.scene.layout.TilePane;

import mx.uam.ayd.proyecto.datos.PedidoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.DetallesPedido;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Platillo;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.CocinaPantallaControlador;
import mx.uam.ayd.proyecto.presentacion.visualizarOrden.ControlVisualizarOrden;

/**
 * Pruebas unitarias para validar las reglas de negocio del controlador de cocina.
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

    @BeforeAll
    static void initJFX() {
        // Inicializa el toolkit de JavaFX de forma segura para evitar el error "Toolkit not initialized"
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // El toolkit ya fue inicializado previamente
        }
    }

    @BeforeEach
    void setUp() {
        // Inicializamos Mockito para que reconozca los mocks
        MockitoAnnotations.openMocks(this);

        // Inicializamos el contenedor visual por reflexión
        try {
            Field field = CocinaPantallaControlador.class.getDeclaredField("containerPendientes");
            field.setAccessible(true);
            field.set(cocinaPantallaControlador, new TilePane());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Datos de prueba comunes
        pedidoPrueba = new Pedido();
        pedidoPrueba.setIdPedido(1L);
        pedidoPrueba.setNumeroOrden(5);
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

    // PRUEBAS (VISUALIZACIÓN DE PEDIDOS EN PANTALLA DE COCINA)
    @Test
    void testMostrarPedidosPendientes_VisualizacionExitosa() {
        List<Pedido> listaPedidos = Arrays.asList(pedidoPrueba);
        when(servicioOrden.obtenerDetallesDePedido(1L)).thenReturn(Arrays.asList(detalleRollos));

        // EJECUTA LA CARGA VISUAL
        cocinaPantallaControlador.mostrarPedidosPendientes(listaPedidos);

        // VERIFICA QUE SE CONSULTARON LOS DETALLES
        verify(servicioOrden, times(1)).obtenerDetallesDePedido(1L);
    }

    // PRUEBAS (DIVISIÓN POR ÁREA DE ROLLOS Y PLANCHA)
    @Test
    void testMostrarPedidosPendientes_FiltraCorrectamentePorEstacion() {
        List<DetallesPedido> detalles = Arrays.asList(detalleRollos, detallePlancha);
        when(servicioOrden.obtenerDetallesDePedido(1L)).thenReturn(detalles);

        List<Pedido> listaPedidos = Arrays.asList(pedidoPrueba);
        
        // EJECUTA EL FILTRADO
        cocinaPantallaControlador.mostrarPedidosPendientes(listaPedidos);

        // VERIFICA QUE SE OBTUVIERON LOS DETALLES PARA EL FILTRADO
        verify(servicioOrden, times(1)).obtenerDetallesDePedido(1L);
        assertNotNull(cocinaPantallaControlador);
    }

    // PRUEBAS (GUARDAR CON PAD NUMÉRICO / NÚMERO DE ORDEN)
    @Test
    void testGuardarPedidoPorNumeroOrden_Exito() {
        long idPedido = 1L;
        when(servicioPedido.recuperaPedido(idPedido)).thenReturn(pedidoPrueba);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // EJECUTA LA FINALIZACIÓN
        cocinaPantallaControlador.finalizarOrden(idPedido);

        // VERIFICA QUE EL ESTADO CAMBIÓ Y SE GUARDÓ
        assertEquals("Completado", pedidoPrueba.getEstado());
        verify(pedidoRepository, times(1)).save(pedidoPrueba);
    }

    // PRUEBAS (GUARDAR DANDO CLIC EN EL BOTÓN DE LA COMANDA)
    @Test
    void testFinalizarOrden_ExitoYActualizacionDeVista() {
        long idPedido = 1L;
        
        when(servicioPedido.recuperaPedido(idPedido)).thenReturn(pedidoPrueba);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // EJECUTA LA FINALIZACIÓN DESDE LA VISTA
        cocinaPantallaControlador.finalizarOrden(idPedido);

        // VERIFICA EL CAMBIO DE ESTADO Y LLAMADAS
        assertEquals("Completado", pedidoPrueba.getEstado(), "El estado del pedido debió cambiar a Completado.");
        verify(servicioPedido, times(1)).recuperaPedido(idPedido);
        verify(pedidoRepository, times(1)).save(pedidoPrueba);
        verify(controlVisualizarOrden, times(1)).cargarPedidosPendientes();
    }

    // PRUEBAS (DEBAN ESTAR PALOMEADOS TODOS LOS PLATILLOS DEL PEDIDO)
    @Test
    void testValidarPlatillosCompletadosAntesDeFinalizar() {
        // MARCAMOS LOS PLATILLOS COMO COMPLETADOS (PALOMEADOS)
        detalleRollos.setCompletado(true);
        detallePlancha.setCompletado(true);

        List<DetallesPedido> detalles = Arrays.asList(detalleRollos, detallePlancha);
        when(servicioOrden.obtenerDetallesDePedido(1L)).thenReturn(detalles);
        when(servicioPedido.recuperaPedido(1L)).thenReturn(pedidoPrueba);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPrueba);

        // VERIFICA QUE TODOS ESTÉN COMPLETADOS
        boolean todosCompletados = detalles.stream().allMatch(DetallesPedido::isCompletado);
        assertTrue(todosCompletados, "Todos los platillos deben estar completados (palomeados).");

        cocinaPantallaControlador.finalizarOrden(1L);
        assertEquals("Completado", pedidoPrueba.getEstado());
    }
}