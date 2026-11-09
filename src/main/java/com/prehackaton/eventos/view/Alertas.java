package com.prehackaton.eventos.view;

import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/** Alertas y diálogos reutilizables (feedback al usuario). */
public final class Alertas {

    private Alertas() {
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(titulo);
        a.setContentText(mensaje);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void advertencia(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void error(String titulo, Exception ex) {
        error(titulo, ex.getMessage() == null ? ex.toString() : ex.getMessage());
    }

    /** Traduce los errores de MySQL más comunes a mensajes comprensibles. */
    public static void errorBd(String contexto, SQLException ex) {
        String detalle;
        String estadoSql = ex.getSQLState() == null ? "" : ex.getSQLState();
        if (estadoSql.startsWith("08")) {
            detalle = "No se pudo conectar con MySQL. Verifique que el servidor esté activo y revise db.properties.";
        } else if (ex.getErrorCode() == 1451) {
            detalle = "No se puede eliminar: el registro tiene información relacionada (inscripciones).";
        } else if (ex.getErrorCode() == 1452) {
            detalle = "El evento o participante indicado ya no existe.";
        } else if (ex.getErrorCode() == 1062) {
            detalle = "Ya existe un registro con esos datos únicos (DNI, correo, evento/fecha o inscripción).";
        } else {
            detalle = ex.getMessage();
        }
        error(contexto, detalle);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        a.setTitle(titulo);
        a.setHeaderText(titulo);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
}
