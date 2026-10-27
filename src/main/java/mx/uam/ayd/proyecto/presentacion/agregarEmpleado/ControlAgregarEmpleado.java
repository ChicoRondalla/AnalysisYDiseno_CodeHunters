package mx.uam.ayd.proyecto.presentacion.agregarEmpleado;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioEmpleado;
import mx.uam.ayd.proyecto.presentacion.tablaEmpleados.ControlTablaEmpleados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Controlador de la ventana modal para registrar un nuevo empleado (HU-03,
 * Escenarios 1 a 4).
 *
 * <p>Se abre desde el botón "Registrar Empleado" de la tabla de personal. Su
 * trabajo es leer lo que el administrador escribe en el formulario, mandárselo
 * a {@link ServicioEmpleado} y mostrar el resultado (error en pantalla o
 * cerrar la ventana si todo salió bien).</p>
 *
 * <h3>Decisiones de diseño (el porqué de las cosas)</h3>
 * <ul>
 *   <li><b>Aquí NO se validan las reglas de negocio.</b> Que el usuario sea
 *       único (Escenario 2), que la contraseña sea fuerte (Escenario 3) o que
 *       no falten campos (Escenario 4) lo decide {@code ServicioEmpleado}, que
 *       avisa lanzando una excepción. El controlador solo muestra el mensaje.
 *       Así las reglas viven en un solo lugar (capa de negocio), se pueden
 *       probar sin JavaFX y no dependen de qué pantalla se use para
 *       registrar.</li>
 *   <li><b>Es un bean singleton y se carga con
 *       {@code setControllerFactory(springContext::getBean)}.</b> Es la misma
 *       razón que en los otros controladores: si el {@code FXMLLoader} creara
 *       el objeto por su cuenta, Spring no inyectaría {@code servicioEmpleado}
 *       y habría un {@code NullPointerException} al guardar. Como el objeto se
 *       reutiliza, también hay que limpiar el formulario cada vez que se abre
 *       (ver {@link #limpiarCampos()}).</li>
 *   <li><b>El rol "Administrador" ya no aparece en el combo.</b> Con el login
 *       de administrador (Escenario 0), los administradores no se crean desde
 *       este formulario. Ojo: quitarlo de la lista solo protege la
 *       <i>interfaz</i>; lo ideal es que {@code ServicioEmpleado} también
 *       rechace ese rol, porque la interfaz no es una barrera de seguridad.</li>
 * </ul>
 */
@Component
public class ControlAgregarEmpleado {

    @Autowired
    private ServicioEmpleado servicioEmpleado;

    @Autowired
    private ApplicationContext springContext;

    // Los campos con @FXML los llena el FXMLLoader a partir de los fx:id del archivo
    // vista-registro-empleado.fxml. Si un nombre no coincide con su fx:id, queda en null.
    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    private Label lblMensaje;

    private Stage stage;

    /**
     * Referencia a la tabla que abrió este formulario. La guardamos para poder
     * pedirle que se refresque en cuanto se guarde el empleado.
     */
    private ControlTablaEmpleados controlTabla;

    /**
     * Lo llama JavaFX solo, justo después de cargar el FXML. Aquí llenamos el
     * combo de roles por código (en el FXML está vacío) para tener la lista en
     * un único lugar fácil de cambiar.
     *
     * <p>Dejamos "Cocinero" seleccionado por defecto. Esto tiene un efecto
     * secundario útil: el combo nunca queda vacío al abrir, así que el caso
     * de "rol sin seleccionar" del Escenario 4 prácticamente no puede ocurrir
     * desde la interfaz (aun así el servicio lo valida por si acaso).</p>
     *
     * <p>El {@code if (comboRol != null)} es defensivo: evita un error si por
     * alguna razón el campo no se inyectó.</p>
     */
    @FXML
    public void initialize() {
        if (comboRol != null) {
            comboRol.setItems(FXCollections.observableArrayList(
                "Recepcionista",
                "Cocinero",
                "Repartidor"
            ));
            comboRol.getSelectionModel().select("Cocinero");
        }
    }

    /**
     * Abre el formulario como ventana modal y se queda esperando hasta que se
     * cierre.
     *
     * <p>Recibe la tabla que lo invoca ({@code controlTabla}) para poder
     * avisarle cuando haya un empleado nuevo. Es una forma sencilla de
     * comunicar dos pantallas sin que se conozcan por dentro.</p>
     *
     * <p>Es {@code APPLICATION_MODAL}: mientras el formulario esté abierto no
     * se puede tocar la tabla, así evitamos registrar dos empleados a la vez o
     * refrescar la tabla a medias.</p>
     *
     * <p>{@code limpiarCampos()} se llama <i>después</i> de {@code load()}
     * porque antes de cargar el FXML los campos todavía son {@code null}.
     * Y se usa {@code showAndWait()} (no {@code show()}) para que esta llamada
     * no regrese hasta que el formulario se cierre.</p>
     *
     * @param controlTabla la tabla de empleados que abrió el formulario y que
     *                     se refrescará tras un registro exitoso.
     */
    public void muestra(ControlTablaEmpleados controlTabla) {
        this.controlTabla = controlTabla;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-registro-empleado.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            stage = new Stage();
            stage.setTitle("Registrar Empleado - Ryuho Sushi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            // Ventana redimensionable: el formulario permanece centrado
            stage.setResizable(true);
            stage.setMinWidth(440);
            stage.setMinHeight(540);
            
            limpiarCampos();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se ejecuta al presionar "Guardar Empleado" (enlazado con {@code onAction}
     * en el FXML). Es el corazón del Escenario 1 y, según lo que responda el
     * servicio, también de los Escenarios 2, 3 y 4.
     *
     * <h4>Flujo</h4>
     * <ol>
     *   <li>Se limpia el mensaje anterior para no mostrar errores viejos.</li>
     *   <li>Se leen los campos y se le entregan al servicio.</li>
     *   <li><b>Éxito:</b> se refresca la tabla <i>antes</i> de cerrar el
     *       formulario. Esto cumple el "aparece inmediatamente en la lista"
     *       del Escenario 1: no hay que reabrir nada.</li>
     *   <li><b>Fallo:</b> la ventana <i>no</i> se cierra y los campos no se
     *       borran, para que el administrador corrija solo lo que está mal
     *       sin volver a escribir todo.</li>
     * </ol>
     *
     * <h4>Por qué hay dos {@code catch}</h4>
     * <ul>
     *   <li>{@code IllegalArgumentException}: es la forma en que el servicio
     *       avisa de un problema de <i>validación</i> (usuario duplicado,
     *       contraseña débil, campo vacío). Esos mensajes están escritos para
     *       el usuario, así que se muestran tal cual.</li>
     *   <li>{@code Exception}: cualquier otra cosa (error de base de datos,
     *       un bug...). Aquí mostramos un mensaje genérico a propósito: el
     *       texto técnico de la excepción no le sirve al administrador y
     *       podría revelar detalles internos del sistema.</li>
     * </ul>
     * <p>El orden importa: {@code IllegalArgumentException} es una subclase de
     * {@code Exception}, así que debe ir primero; si se invirtiera, el catch
     * general se "comería" todas las validaciones.</p>
     *
     * <p>El color rojo se vuelve a asignar con {@code setStyle} aunque el FXML
     * ya lo define, por si en algún momento se reutiliza el mismo label para
     * mensajes con otro color (por ejemplo, uno de éxito en verde).</p>
     */
    @FXML
    public void actionGuardar() {
        lblMensaje.setText("");
        String nombre = txtNombre.getText();
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();
        String rol = comboRol.getValue();

        try {
            // Todas las validaciones (usuario único, contraseña fuerte, campos obligatorios)
            // las hace ServicioEmpleado; si algo no cumple, lanza IllegalArgumentException.
            servicioEmpleado.registrarEmpleado(nombre, usuario, contrasena, rol);
            
            if (controlTabla != null) {
                controlTabla.actualizarTabla();
            }
            stage.close();
        } catch (IllegalArgumentException e) {
            lblMensaje.setStyle("-fx-text-fill: #ef4444;");
            lblMensaje.setText(e.getMessage());
        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: #ef4444;");
            lblMensaje.setText("Error al guardar el empleado. Intente de nuevo.");
        }
    }

    /**
     * Se ejecuta al presionar "Cancelar". Solo cierra la ventana: no se guarda
     * nada y la tabla no se refresca porque no hubo cambios. El chequeo de
     * {@code null} es defensivo, por si se llama antes de que exista el
     * {@code Stage}.
     */
    @FXML
    public void actionCancelar() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Deja el formulario como nuevo: campos vacíos, rol en "Cocinero" y sin
     * mensaje de error.
     *
     * <p>Como este controlador es singleton y se reutiliza, sin esto podría
     * verse información de un registro anterior. Cada {@code if} revisa
     * {@code null} porque este método puede ejecutarse antes de que todos los
     * controles estén inyectados.</p>
     */
    private void limpiarCampos() {
        if (txtNombre != null) txtNombre.clear();
        if (txtUsuario != null) txtUsuario.clear();
        if (txtContrasena != null) txtContrasena.clear();
        if (comboRol != null) comboRol.getSelectionModel().select("Cocinero");
        if (lblMensaje != null) lblMensaje.setText("");
    }
}