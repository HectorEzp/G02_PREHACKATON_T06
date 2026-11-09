package com.prehackaton.eventos.view;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VentanaPrincipal extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent raiz = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
        Scene escena = new Scene(raiz, 1280, 760);
        stage.setTitle("Gestión de Eventos y Conferencias — Pre-Hackatón T06");
        stage.setMinWidth(1100);
        stage.setMinHeight(640);
        stage.setScene(escena);
        stage.show();
    }

}
