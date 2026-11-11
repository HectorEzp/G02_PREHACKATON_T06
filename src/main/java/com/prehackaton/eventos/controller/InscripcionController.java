package com.prehackaton.eventos.controller;

import com.prehackaton.eventos.model.dao.EventoDAO;
import com.prehackaton.eventos.model.dao.EventoDAOImpl;
import com.prehackaton.eventos.model.dao.InscripcionDAO;
import com.prehackaton.eventos.model.dao.InscripcionDAOImpl;
import com.prehackaton.eventos.model.dao.ParticipanteDAO;
import com.prehackaton.eventos.model.dao.ParticipanteDAOImpl;
import com.prehackaton.eventos.model.Evento;
import com.prehackaton.eventos.model.Inscripcion;
import com.prehackaton.eventos.model.Participante;
import com.prehackaton.eventos.view.Alertas;
import com.prehackaton.eventos.view.Formularios;
import com.prehackaton.eventos.view.ReporteDialog;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/** Módulo Integrante 3: Gestión de Inscripciones (matrícula y vínculos evento-participante). */
public class InscripcionController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Inscripcion> tblInscripciones;
    @FXML private TableColumn<Inscripcion, Integer> colId;
    @FXML private TableColumn<Inscripcion, String> colEvento;
    @FXML private TableColumn<Inscripcion, String> colParticipante;
    @FXML private TableColumn<Inscripcion, String> colDni;
    @FXML private TableColumn<Inscripcion, String> colFecha;
    @FXML private TableColumn<Inscripcion, String> colEstado;

    @FXML private ComboBox<Evento> cmbEvento;
    @FXML private ComboBox<Participante> cmbParticipante;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    private final InscripcionDAO dao = new InscripcionDAOImpl();
    private final EventoDAO eventoDao = new EventoDAOImpl();
    private final ParticipanteDAO participanteDao = new ParticipanteDAOImpl();
    private final ObservableList<Inscripcion> datos = FXCollections.observableArrayList();
    private Inscripcion seleccionada;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idInscripcion"));
        colEvento.setCellValueFactory(new PropertyValueFactory<>("eventoNombre"));
        colParticipante.setCellValueFactory(new PropertyValueFactory<>("participanteNombre"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("participanteDni"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaTexto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblInscripciones.setItems(datos);

        cmbEstado.setItems(FXCollections.observableArrayList(Inscripcion.ESTADOS));
        Formularios.configurarDatePicker(dpFecha);

        txtBuscar.textProperty().addListener((obs, a, b) -> cargar());
        tblInscripciones.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            if (b != null) {
                mostrar(b);
            }
        });

        cargarCombos();
        limpiar();
        cargar();
    }

    private void cargarCombos() {
        try {
            cmbEvento.setItems(FXCollections.observableArrayList(eventoDao.listar()));
            cmbParticipante.setItems(FXCollections.observableArrayList(participanteDao.listar()));
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudieron cargar eventos y participantes", ex);
        }
    }

    private void cargar() {
        try {
            datos.setAll(dao.buscar(txtBuscar.getText()));
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudieron cargar las inscripciones", ex);
        }
    }

    private void mostrar(Inscripcion i) {
        seleccionada = i;
        cmbEvento.setValue(cmbEvento.getItems().stream()
                .filter(e -> e.getIdEvento() == i.getIdEvento()).findFirst().orElse(null));
        cmbParticipante.setValue(cmbParticipante.getItems().stream()
                .filter(p -> p.getIdParticipante() == i.getIdParticipante()).findFirst().orElse(null));
        dpFecha.setValue(i.getFechaInscripcion().toLocalDate());
        cmbEstado.setValue(i.getEstado());
        btnGuardar.setDisable(true);
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
    }

    @FXML
    private void onLimpiar() {
        limpiar();
        tblInscripciones.getSelectionModel().clearSelection();
    }

    private void limpiar() {
        seleccionada = null;
        cmbEvento.setValue(null);
        cmbParticipante.setValue(null);
        dpFecha.setValue(LocalDate.now());
        cmbEstado.setValue("Inscrito");
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private Inscripcion leerFormulario(int id) {
        List<String> errores = new ArrayList<>();
        Evento evento = cmbEvento.getValue();
        Participante participante = cmbParticipante.getValue();
        LocalDate fecha = Formularios.leerFecha(dpFecha);

        if (evento == null) {
            errores.add("• Seleccione un evento.");
        }
        if (participante == null) {
            errores.add("• Seleccione un participante.");
        }
        if (fecha == null) {
            errores.add("• Seleccione una fecha de inscripción válida (dd/mm/aaaa).");
        }
        if (cmbEstado.getValue() == null) {
            errores.add("• Seleccione el estado de la inscripción.");
        }
        if (!errores.isEmpty()) {
            Alertas.advertencia("Revise los datos del formulario", String.join("\n", errores));
            return null;
        }

        Inscripcion i = new Inscripcion();
        i.setIdInscripcion(id);
        i.setIdEvento(evento.getIdEvento());
        i.setIdParticipante(participante.getIdParticipante());
        LocalTime hora = (seleccionada != null && seleccionada.getFechaInscripcion() != null && id != 0)
                ? seleccionada.getFechaInscripcion().toLocalTime()
                : LocalTime.now().withNano(0);
        i.setFechaInscripcion(LocalDateTime.of(fecha, hora));
        i.setEstado(cmbEstado.getValue());
        return i;
    }

    /** Regla de negocio: no se aceptan nuevas inscripciones en eventos finalizados o cancelados. */
    private boolean eventoAceptaInscripciones(Evento e) {
        if ("Finalizado".equals(e.getEstado()) || "Cancelado".equals(e.getEstado())) {
            Alertas.advertencia("Evento no disponible",
                    "El evento \"" + e.getNombre() + "\" está " + e.getEstado().toLowerCase()
                            + " y no admite nuevas inscripciones.");
            return false;
        }
        return true;
    }

    @FXML
    private void onGuardar() {
        Inscripcion i = leerFormulario(0);
        if (i == null) {
            return;
        }
        try {
            if (!eventoAceptaInscripciones(cmbEvento.getValue())) {
                return;
            }
            if (dao.existeInscripcion(i.getIdEvento(), i.getIdParticipante(), 0)) {
                Alertas.advertencia("Inscripción duplicada", "Este participante ya está inscrito en el evento seleccionado.");
                return;
            }
            if (dao.insertar(i)) {
                Alertas.info("Inscripción registrada", "La inscripción se guardó correctamente.");
                onLimpiar();
                cargar();
            }
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo registrar la inscripción", ex);
        }
    }

    @FXML
    private void onActualizar() {
        if (seleccionada == null) {
            Alertas.advertencia("Seleccione una inscripción", "Elija una inscripción de la tabla para actualizarla.");
            return;
        }
        Inscripcion i = leerFormulario(seleccionada.getIdInscripcion());
        if (i == null) {
            return;
        }
        try {
            if (dao.buscarPorId(i.getIdInscripcion()) == null) {
                Alertas.advertencia("Inscripción inexistente", "La inscripción ya no existe en la base de datos.");
                onLimpiar();
                cargar();
                return;
            }
            if (dao.existeInscripcion(i.getIdEvento(), i.getIdParticipante(), i.getIdInscripcion())) {
                Alertas.advertencia("Inscripción duplicada", "Este participante ya está inscrito en el evento seleccionado.");
                return;
            }
            if (dao.actualizar(i)) {
                Alertas.info("Inscripción actualizada", "Los cambios se guardaron correctamente.");
                onLimpiar();
                cargar();
            }
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo actualizar la inscripción", ex);
        }
    }

    @FXML
    private void onEliminar() {
        if (seleccionada == null) {
            Alertas.advertencia("Seleccione una inscripción", "Elija una inscripción de la tabla para eliminarla.");
            return;
        }
        if (!Alertas.confirmar("Confirmar eliminación",
                "¿Eliminar la inscripción de \"" + seleccionada.getParticipanteNombre() + "\" en \""
                        + seleccionada.getEventoNombre() + "\"?")) {
            return;
        }
        try {
            if (dao.eliminar(seleccionada.getIdInscripcion())) {
                Alertas.info("Inscripción eliminada", "La inscripción fue eliminada correctamente.");
            }
            onLimpiar();
            cargar();
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo eliminar la inscripción", ex);
        }
    }

    @FXML
    private void onRefrescar() {
        cargarCombos();
        cargar();
    }

    @FXML
    private void onReporte() {
        List<Evento> opciones = new ArrayList<>();
        Evento todos = new Evento();
        todos.setIdEvento(0);
        todos.setNombre("— Todos los eventos —");
        opciones.add(todos);
        try {
            opciones.addAll(eventoDao.listar());
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudieron cargar los eventos", ex);
            return;
        }
        ChoiceDialog<Evento> dialogo = new ChoiceDialog<>(todos, opciones);
        dialogo.setTitle("Reporte de Inscritos por Evento");
        dialogo.setHeaderText("Seleccione el evento a reportar");
        dialogo.setContentText("Evento:");
        dialogo.showAndWait().ifPresent(evento -> {
            Map<String, Object> params = new HashMap<>();
            params.put("ID_EVENTO", evento.getIdEvento());
            ReporteDialog.mostrar("Reporte de Inscritos por Evento", "reporte_inscritos_evento", params);
        });
    }
}
