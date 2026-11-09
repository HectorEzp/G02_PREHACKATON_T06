package com.prehackaton.eventos.controller;

import com.prehackaton.eventos.model.dao.ParticipanteDAO;
import com.prehackaton.eventos.model.dao.ParticipanteDAOImpl;
import com.prehackaton.eventos.model.Participante;
import com.prehackaton.eventos.view.Alertas;
import com.prehackaton.eventos.view.ReporteDialog;
import com.prehackaton.eventos.controller.Validador;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/** Módulo Integrante 2: Gestión de Participantes (registro de datos y perfil). */
public class ParticipanteController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Participante> tblParticipantes;
    @FXML private TableColumn<Participante, Integer> colId;
    @FXML private TableColumn<Participante, String> colNombres;
    @FXML private TableColumn<Participante, String> colApellidos;
    @FXML private TableColumn<Participante, String> colDni;
    @FXML private TableColumn<Participante, String> colCorreo;
    @FXML private TableColumn<Participante, String> colTelefono;
    @FXML private TableColumn<Participante, String> colInstitucion;

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtInstitucion;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    private final ParticipanteDAO dao = new ParticipanteDAOImpl();
    private final ObservableList<Participante> datos = FXCollections.observableArrayList();
    private Participante seleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idParticipante"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colInstitucion.setCellValueFactory(new PropertyValueFactory<>("institucion"));
        tblParticipantes.setItems(datos);

        txtBuscar.textProperty().addListener((obs, a, b) -> cargar());
        tblParticipantes.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> {
            if (b != null) {
                mostrar(b);
            }
        });
        // Solo dígitos y longitud máxima en DNI y teléfono
        soloDigitos(txtDni, 8);
        soloDigitos(txtTelefono, 9);

        limpiar();
        cargar();
    }

    private void soloDigitos(TextField campo, int maximo) {
        campo.textProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null && (!nuevo.matches("\\d*") || nuevo.length() > maximo)) {
                campo.setText(anterior);
            }
        });
    }

    private void cargar() {
        try {
            datos.setAll(dao.buscar(txtBuscar.getText()));
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudieron cargar los participantes", ex);
        }
    }

    private void mostrar(Participante p) {
        seleccionado = p;
        txtNombres.setText(p.getNombres());
        txtApellidos.setText(p.getApellidos());
        txtDni.setText(p.getDni());
        txtCorreo.setText(p.getCorreo());
        txtTelefono.setText(p.getTelefono());
        txtInstitucion.setText(p.getInstitucion());
        btnGuardar.setDisable(true);
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
    }

    @FXML
    private void onLimpiar() {
        limpiar();
        tblParticipantes.getSelectionModel().clearSelection();
    }

    private void limpiar() {
        seleccionado = null;
        txtNombres.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtInstitucion.clear();
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private Participante leerFormulario(int id) {
        List<String> errores = new ArrayList<>();
        String nombres = Validador.limpiar(txtNombres.getText());
        String apellidos = Validador.limpiar(txtApellidos.getText());
        String dni = Validador.limpiar(txtDni.getText());
        String correo = Validador.limpiar(txtCorreo.getText());
        String telefono = Validador.limpiar(txtTelefono.getText());
        String institucion = Validador.limpiar(txtInstitucion.getText());

        if (nombres.isEmpty()) {
            errores.add("• Los nombres son obligatorios.");
        } else if (!Validador.esNombre(nombres) || nombres.length() > 80) {
            errores.add("• Los nombres solo pueden contener letras (máx. 80).");
        }
        if (apellidos.isEmpty()) {
            errores.add("• Los apellidos son obligatorios.");
        } else if (!Validador.esNombre(apellidos) || apellidos.length() > 80) {
            errores.add("• Los apellidos solo pueden contener letras (máx. 80).");
        }
        if (dni.isEmpty()) {
            errores.add("• El DNI es obligatorio.");
        } else if (!Validador.esDni(dni)) {
            errores.add("• El DNI debe tener exactamente 8 dígitos numéricos.");
        }
        if (correo.isEmpty()) {
            errores.add("• El correo es obligatorio.");
        } else if (!Validador.esCorreo(correo) || correo.length() > 100) {
            errores.add("• El correo no tiene un formato válido (ejemplo usuario@dominio.com).");
        }
        if (!telefono.isEmpty() && !Validador.esTelefono(telefono)) {
            errores.add("• El teléfono debe tener 9 dígitos numéricos.");
        }
        if (institucion.isEmpty()) {
            errores.add("• La institución es obligatoria.");
        } else if (institucion.length() > 120) {
            errores.add("• La institución no puede superar 120 caracteres.");
        }
        if (!errores.isEmpty()) {
            Alertas.advertencia("Revise los datos del formulario", String.join("\n", errores));
            return null;
        }

        Participante p = new Participante();
        p.setIdParticipante(id);
        p.setNombres(nombres);
        p.setApellidos(apellidos);
        p.setDni(dni);
        p.setCorreo(correo);
        p.setTelefono(telefono.isEmpty() ? null : telefono);
        p.setInstitucion(institucion);
        return p;
    }

    /** @return true si no hay duplicados de DNI ni correo. */
    private boolean sinDuplicados(Participante p) throws SQLException {
        if (dao.existeDni(p.getDni(), p.getIdParticipante())) {
            Alertas.advertencia("DNI duplicado", "Ya existe un participante registrado con el DNI " + p.getDni() + ".");
            return false;
        }
        if (dao.existeCorreo(p.getCorreo(), p.getIdParticipante())) {
            Alertas.advertencia("Correo duplicado", "Ya existe un participante con el correo " + p.getCorreo() + ".");
            return false;
        }
        return true;
    }

    @FXML
    private void onGuardar() {
        Participante p = leerFormulario(0);
        if (p == null) {
            return;
        }
        try {
            if (sinDuplicados(p) && dao.insertar(p)) {
                Alertas.info("Participante registrado", "El participante se guardó correctamente.");
                onLimpiar();
                cargar();
            }
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo registrar el participante", ex);
        }
    }

    @FXML
    private void onActualizar() {
        if (seleccionado == null) {
            Alertas.advertencia("Seleccione un participante", "Elija un participante de la tabla para actualizarlo.");
            return;
        }
        Participante p = leerFormulario(seleccionado.getIdParticipante());
        if (p == null) {
            return;
        }
        try {
            if (dao.buscarPorId(p.getIdParticipante()) == null) {
                Alertas.advertencia("Participante inexistente", "El participante ya no existe en la base de datos.");
                onLimpiar();
                cargar();
                return;
            }
            if (sinDuplicados(p) && dao.actualizar(p)) {
                Alertas.info("Participante actualizado", "Los cambios se guardaron correctamente.");
                onLimpiar();
                cargar();
            }
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo actualizar el participante", ex);
        }
    }

    @FXML
    private void onEliminar() {
        if (seleccionado == null) {
            Alertas.advertencia("Seleccione un participante", "Elija un participante de la tabla para eliminarlo.");
            return;
        }
        if (!Alertas.confirmar("Confirmar eliminación",
                "¿Eliminar definitivamente a \"" + seleccionado.getNombreCompleto() + "\"?\nEsta acción no se puede deshacer.")) {
            return;
        }
        try {
            if (dao.eliminar(seleccionado.getIdParticipante())) {
                Alertas.info("Participante eliminado", "El participante fue eliminado correctamente.");
            }
            onLimpiar();
            cargar();
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudo eliminar el participante", ex);
        }
    }

    @FXML
    private void onRefrescar() {
        cargar();
    }

    @FXML
    private void onReporte() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Todas");
        try {
            opciones.addAll(dao.listarInstituciones());
        } catch (SQLException ex) {
            Alertas.errorBd("No se pudieron cargar las instituciones", ex);
            return;
        }
        ChoiceDialog<String> dialogo = new ChoiceDialog<>("Todas", opciones);
        dialogo.setTitle("Reporte de Participantes por Institución");
        dialogo.setHeaderText("Filtrar por institución");
        dialogo.setContentText("Institución:");
        dialogo.showAndWait().ifPresent(inst -> {
            Map<String, Object> params = new HashMap<>();
            params.put("INSTITUCION", inst);
            ReporteDialog.mostrar("Reporte de Participantes por Institución", "reporte_participantes_institucion", params);
        });
    }
}
