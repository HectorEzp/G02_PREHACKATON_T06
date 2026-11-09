package com.prehackaton.eventos.controller;

import com.prehackaton.eventos.model.dao.EventoDAO;
import com.prehackaton.eventos.model.dao.EventoDAOImpl;
import com.prehackaton.eventos.model.Evento;
import com.prehackaton.eventos.view.Alertas;
import com.prehackaton.eventos.view.Formularios;
import com.prehackaton.eventos.view.ReporteDialog;
import com.prehackaton.eventos.controller.Validador;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/** Módulo Integrante 1: Gestión de Eventos (crear, actualizar, listar, cambiar estado, eliminar). */
public class EventoController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TableView<Evento> tblEventos;
    @FXML private TableColumn<Evento, Integer> colId;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colLugar;
    @FXML private TableColumn<Evento, String> colModalidad;
    @FXML private TableColumn<Evento, String> colEstado;

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtLugar;
    @FXML private ComboBox<String> cmbModalidad;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    private final EventoDAO dao = new EventoDAOImpl();
    private final ObservableList<Evento> datos = FXCollections.observableArrayList();
    private Evento seleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEvento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaTexto"));
        colLugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
        colModalidad.setCellValueFactory(new PropertyValueFactory<>("modalidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblEventos.setItems(datos);

        cmbModalidad.setItems(FXCollections.observableArrayList(Evento.MODALIDADES));
        cmbEstado.setItems(FXCollections.observableArrayList(Evento.ESTADOS));
        List<String> filtros = new ArrayList<>();
        filtros.add("Todos");
        filtros.addAll(Evento.ESTADOS);
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(filtros));
        cmbFiltroEstado.getSelectionModel().selectFirst();
        Formularios.configurarDatePicker(dpFecha);

        // Filtro dinámico: se recarga al escribir o al cambiar el estado
        txtBuscar.textProperty().addListener((obs, a, b) -> cargar());
        cmbFiltroEstado.valueProperty().addListener((obs, a, b) -> cargar());
        tblEventos.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            if (b != null) {
                mostrar(b);
            }
        });

        limpiar();
        cargar();
    }

    private void cargar() {
        try {
            List<Evento> lista = dao.buscar(txtBuscar.getText());
            String estado = cmbFiltroEstado.getValue();
            if (estado != null && !estado.equals("Todos")) {
                lista = lista.stream().filter(e -> estado.equals(e.getEstado())).toList();
            }
            datos.setAll(lista);
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudieron cargar los eventos", ex);
        }
    }

    private void mostrar(Evento e) {
        seleccionado = e;
        txtNombre.setText(e.getNombre());
        txtDescripcion.setText(e.getDescripcion());
        dpFecha.setValue(e.getFecha().toLocalDate());
        txtHora.setText(e.getFecha().toLocalTime().toString().substring(0, 5));
        txtLugar.setText(e.getLugar());
        cmbModalidad.setValue(e.getModalidad());
        cmbEstado.setValue(e.getEstado());
        btnGuardar.setDisable(true);
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
    }

    @FXML
    private void onLimpiar() {
        limpiar();
        tblEventos.getSelectionModel().clearSelection();
    }

    private void limpiar() {
        seleccionado = null;
        txtNombre.clear();
        txtDescripcion.clear();
        dpFecha.setValue(null);
        dpFecha.getEditor().clear();
        txtHora.setText("09:00");
        txtLugar.clear();
        cmbModalidad.setValue(null);
        cmbEstado.setValue("Programado");
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    /** Valida los campos y construye el Evento; devuelve null si hay errores (ya informados). */
    private Evento leerFormulario(int id) {
        List<String> errores = new ArrayList<>();
        String nombre = Validador.limpiar(txtNombre.getText());
        String descripcion = Validador.limpiar(txtDescripcion.getText());
        String lugar = Validador.limpiar(txtLugar.getText());
        LocalDate fecha = Formularios.leerFecha(dpFecha);
        LocalTime hora = Validador.parseHora(txtHora.getText());

        if (nombre.isEmpty()) {
            errores.add("• El nombre del evento es obligatorio.");
        } else if (nombre.length() > 120) {
            errores.add("• El nombre no puede superar 120 caracteres.");
        }
        if (descripcion.length() > 500) {
            errores.add("• La descripción no puede superar 500 caracteres.");
        }
        if (fecha == null) {
            errores.add("• Seleccione una fecha válida (dd/mm/aaaa).");
        }
        if (hora == null) {
            errores.add("• La hora debe tener formato HH:mm (ejemplo 09:30).");
        }
        if (lugar.isEmpty()) {
            errores.add("• El lugar es obligatorio.");
        } else if (lugar.length() > 120) {
            errores.add("• El lugar no puede superar 120 caracteres.");
        }
        if (cmbModalidad.getValue() == null) {
            errores.add("• Seleccione la modalidad.");
        }
        if (cmbEstado.getValue() == null) {
            errores.add("• Seleccione el estado.");
        }
        if (!errores.isEmpty()) {
            Alertas.advertencia("Revise los datos del formulario", String.join("\n", errores));
            return null;
        }

        Evento e = new Evento();
        e.setIdEvento(id);
        e.setNombre(nombre);
        e.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        e.setFecha(fecha.atTime(hora));
        e.setLugar(lugar);
        e.setModalidad(cmbModalidad.getValue());
        e.setEstado(cmbEstado.getValue());
        return e;
    }

    @FXML
    private void onGuardar() {
        Evento e = leerFormulario(0);
        if (e == null) {
            return;
        }
        try {
            if (dao.existeDuplicado(e.getNombre(), e.getFecha(), 0)) {
                Alertas.advertencia("Evento duplicado", "Ya existe un evento con el mismo nombre y fecha.");
                return;
            }
            if (dao.insertar(e)) {
                Alertas.info("Evento registrado", "El evento se guardó correctamente.");
                onLimpiar();
                cargar();
            }
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo registrar el evento", ex);
        }
    }

    @FXML
    private void onActualizar() {
        if (seleccionado == null) {
            Alertas.advertencia("Seleccione un evento", "Elija un evento de la tabla para actualizarlo.");
            return;
        }
        Evento e = leerFormulario(seleccionado.getIdEvento());
        if (e == null) {
            return;
        }
        try {
            if (dao.buscarPorId(e.getIdEvento()) == null) {
                Alertas.advertencia("Evento inexistente", "El evento ya no existe en la base de datos.");
                onLimpiar();
                cargar();
                return;
            }
            if (dao.existeDuplicado(e.getNombre(), e.getFecha(), e.getIdEvento())) {
                Alertas.advertencia("Evento duplicado", "Ya existe otro evento con el mismo nombre y fecha.");
                return;
            }
            if (dao.actualizar(e)) {
                Alertas.info("Evento actualizado", "Los cambios se guardaron correctamente.");
                onLimpiar();
                cargar();
            }
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo actualizar el evento", ex);
        }
    }

    @FXML
    private void onEliminar() {
        if (seleccionado == null) {
            Alertas.advertencia("Seleccione un evento", "Elija un evento de la tabla para eliminarlo.");
            return;
        }
        if (!Alertas.confirmar("Confirmar eliminación",
                "¿Eliminar definitivamente el evento \"" + seleccionado.getNombre() + "\"?\nEsta acción no se puede deshacer.")) {
            return;
        }
        try {
            if (dao.eliminar(seleccionado.getIdEvento())) {
                Alertas.info("Evento eliminado", "El evento fue eliminado correctamente.");
            }
            onLimpiar();
            cargar();
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo eliminar el evento", ex);
        }
    }

    @FXML
    private void onRefrescar() {
        cargar();
    }

    @FXML
    private void onReporte() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Todos");
        opciones.addAll(Evento.ESTADOS);
        ChoiceDialog<String> dialogo = new ChoiceDialog<>("Todos", opciones);
        dialogo.setTitle("Reporte General de Eventos y Estados");
        dialogo.setHeaderText("Filtrar por estado del evento");
        dialogo.setContentText("Estado:");
        dialogo.showAndWait().ifPresent(estado -> {
            Map<String, Object> params = new HashMap<>();
            params.put("ESTADO", estado);
            ReporteDialog.mostrar("Reporte General de Eventos y Estados", "reporte_eventos", params);
        });
    }
}
