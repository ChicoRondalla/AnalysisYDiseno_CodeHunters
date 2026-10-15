package mx.uam.ayd.proyecto.presentacion.tablaEmpleados;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mx.uam.ayd.proyecto.negocio.ServicioEmpleado;
import mx.uam.ayd.proyecto.negocio.modelo.Empleado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controlador de la vista de la tabla de empleados en JavaFX.
 * Muestra el listado de empleados registrados con un disenio oscuro moderno
 * que incluye avatares, roles dinamicos y acciones.
 * 
 * Cumple con DoD #5 (Documentacion de codigo).
 */
@Component
public class ControlTablaEmpleados {

    @Autowired
    private ServicioEmpleado servicioEmpleado;

    @FXML
    private TableView<Empleado> tablaEmpleados;

    @FXML
    private TableColumn<Empleado, Empleado> columnaNombre;

    @FXML
    private TableColumn<Empleado, String> columnaUsuario;

    @FXML
    private TableColumn<Empleado, String> columnaRol;

    @FXML
    private TableColumn<Empleado, Empleado> columnaAcciones;

    @FXML
    private Label lblConteoEmpleados;

    @FXML
    private TextField txtBuscar;

    /**
     * Inicializa los componentes de la tabla y configura las celdas personalizadas.
     */
    @FXML
    public void initialize() {
        configurarColumnaNombre();
        configurarColumnaUsuario();
        configurarColumnaRol();
        configurarColumnaAcciones();

        actualizarTabla();
    }

    /**
     * Configura la columna Nombre Completo con Avatar (Iniciales) y formato visual.
     */
    private void configurarColumnaNombre() {
        columnaNombre.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        columnaNombre.setCellFactory(column -> new TableCell<Empleado, Empleado>() {
            @Override
            protected void updateItem(Empleado emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setGraphic(null);
                } else {
                    HBox contenedor = new HBox(12);
                    contenedor.setAlignment(Pos.CENTER_LEFT);

                    // Avatar con Iniciales
                    String iniciales = obtenerIniciales(emp.getNombre());
                    Label lblAvatar = new Label(iniciales);
                    lblAvatar.setStyle(obtenerEstiloAvatar(emp.getRol()));
                    lblAvatar.setMinSize(36, 36);
                    lblAvatar.setMaxSize(36, 36);
                    lblAvatar.setAlignment(Pos.CENTER);

                    // Nombre y Subtitulo
                    VBox textContainer = new VBox(2);
                    Label lblNombre = new Label(emp.getNombre());
                    lblNombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

                    Label lblSubtitulo = new Label("Asignado • Turno General");
                    lblSubtitulo.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

                    textContainer.getChildren().addAll(lblNombre, lblSubtitulo);
                    contenedor.getChildren().addAll(lblAvatar, textContainer);

                    setGraphic(contenedor);
                }
            }
        });
    }

    /**
     * Configura la columna de Nombre de Usuario antecedida por '@'.
     */
    private void configurarColumnaUsuario() {
        columnaUsuario.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getUsuario()));
        columnaUsuario.setCellFactory(column -> new TableCell<Empleado, String>() {
            @Override
            protected void updateItem(String usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                } else {
                    setText("@" + usuario);
                    setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 13px; -fx-alignment: CENTER-LEFT;");
                }
            }
        });
    }

    /**
     * Configura la columna de Rol con formato de pill badge y punto de color.
     */
    private void configurarColumnaRol() {
        columnaRol.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getRol()));
        columnaRol.setCellFactory(column -> new TableCell<Empleado, String>() {
            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setGraphic(null);
                } else {
                    HBox badge = new HBox(6);
                    badge.setAlignment(Pos.CENTER_LEFT);
                    badge.setStyle(obtenerEstiloBadgeRol(rol));

                    Circle punto = new Circle(3, Color.web(obtenerColorPuntoRol(rol)));
                    Label lblRol = new Label(rol);
                    lblRol.setStyle("-fx-text-fill: " + obtenerColorTextoRol(rol) + "; -fx-font-weight: bold; -fx-font-size: 11px;");

                    badge.getChildren().addAll(punto, lblRol);
                    setGraphic(badge);
                }
            }
        });
    }

    /**
     * Configura la columna de acciones (Iconos de Editar / Desactivar).
     */
    private void configurarColumnaAcciones() {
        columnaAcciones.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        columnaAcciones.setCellFactory(column -> new TableCell<Empleado, Empleado>() {
            @Override
            protected void updateItem(Empleado emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setGraphic(null);
                } else {
                    HBox acciones = new HBox(10);
                    acciones.setAlignment(Pos.CENTER);

                    Button btnEditar = new Button("✏");
                    btnEditar.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-cursor: hand; -fx-font-size: 13px;");

                    Button btnDesactivar = new Button("🚫");
                    btnDesactivar.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-cursor: hand; -fx-font-size: 13px;");

                    acciones.getChildren().addAll(btnEditar, btnDesactivar);
                    setGraphic(acciones);
                }
            }
        });
    }

    /**
     * Consulta el servicio de negocio y recarga los datos en la TableView.
     */
    public void actualizarTabla() {
        if (servicioEmpleado != null && tablaEmpleados != null) {
            List<Empleado> lista = servicioEmpleado.recuperarEmpleados();
            ObservableList<Empleado> datos = FXCollections.observableArrayList(lista);
            tablaEmpleados.setItems(datos);

            if (lblConteoEmpleados != null) {
                lblConteoEmpleados.setText("Mostrando " + lista.size() + " empleados registrados");
            }
        }
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "EM";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        }
        return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
    }

    private String obtenerEstiloAvatar(String rol) {
        String base = "-fx-background-radius: 50%; -fx-font-weight: bold; -fx-font-size: 12px; ";
        switch (rol.toLowerCase()) {
            case "cocinero": return base + "-fx-background-color: #3f2a1d; -fx-text-fill: #f59e0b;";
            case "administrador": return base + "-fx-background-color: #321c42; -fx-text-fill: #c084fc;";
            case "recepcionista": return base + "-fx-background-color: #1a3848; -fx-text-fill: #38bdf8;";
            case "repartidor": return base + "-fx-background-color: #19382c; -fx-text-fill: #4ade80;";
            default: return base + "-fx-background-color: #2a2d34; -fx-text-fill: #e5e7eb;";
        }
    }

    private String obtenerEstiloBadgeRol(String rol) {
        String base = "-fx-padding: 4 10 4 10; -fx-background-radius: 12; ";
        switch (rol.toLowerCase()) {
            case "cocinero": return base + "-fx-background-color: #3b2818;";
            case "administrador": return base + "-fx-background-color: #2f193e;";
            case "recepcionista": return base + "-fx-background-color: #143140;";
            case "repartidor": return base + "-fx-background-color: #143226;";
            default: return base + "-fx-background-color: #2a2d34;";
        }
    }

    private String obtenerColorPuntoRol(String rol) {
        switch (rol.toLowerCase()) {
            case "cocinero": return "#f59e0b";
            case "administrador": return "#c084fc";
            case "recepcionista": return "#38bdf8";
            case "repartidor": return "#4ade80";
            default: return "#9ca3af";
        }
    }

    private String obtenerColorTextoRol(String rol) {
        return obtenerColorPuntoRol(rol);
    }
}
