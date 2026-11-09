package com.prehackaton.eventos.controller;

import com.prehackaton.eventos.model.dao.Conexion;
import com.prehackaton.eventos.view.Alertas;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/** Navegación principal: un solo ejecutable con módulos intercambiables. */
public class MainController {

    private static final String NAV_BASE = "-fx-alignment:CENTER_LEFT; -fx-font-size:14px; -fx-padding:11 14; -fx-background-radius:8; -fx-cursor:hand;";
    private static final String NAV_NORMAL = "-fx-background-color:transparent; -fx-text-fill:#E0F2FE; " + NAV_BASE;
    private static final String NAV_ACTIVO = "-fx-background-color:white; -fx-text-fill:#075985; -fx-font-weight:bold; " + NAV_BASE;

    @FXML private StackPane contenedor;
    @FXML private Label lblEstadoBd;
    @FXML private Button btnEventos;
    @FXML private Button btnParticipantes;
    @FXML private Button btnInscripciones;

    @FXML
    private void initialize() {
        verificarConexion();
        irEventos();
    }

    private void verificarConexion() {
        String error = Conexion.probar();
        if (error == null) {
            lblEstadoBd.setText("● MySQL conectado (db_eventos)");
            lblEstadoBd.setStyle("-fx-text-fill:#86EFAC; -fx-font-size:12px;");
        } else {
            lblEstadoBd.setText("● Sin conexión a MySQL");
            lblEstadoBd.setStyle("-fx-text-fill:#FCA5A5; -fx-font-size:12px; -fx-font-weight:bold;");
            Alertas.error("No se pudo conectar a la base de datos",
                    "Verifique que MySQL esté activo y que db.properties sea correcto.\n\nDetalle: " + error);
        }
    }

    @FXML private void irEventos() { cargar("eventos", btnEventos); }
    @FXML private void irParticipantes() { cargar("participantes", btnParticipantes); }
    @FXML private void irInscripciones() { cargar("inscripciones", btnInscripciones); }

    private void cargar(String vista, Button activo) {
        try {
            Node nodo = FXMLLoader.load(getClass().getResource("/view/" + vista + ".fxml"));
            contenedor.getChildren().setAll(nodo);
            for (Button b : List.of(btnEventos, btnParticipantes, btnInscripciones)) {
                b.setStyle(NAV_NORMAL);
            }
            activo.setStyle(NAV_ACTIVO);
        } catch (IOException e) {
            Alertas.error("No se pudo cargar el módulo", e);
        }
    }
}
