package com.prehackaton.eventos;

import com.prehackaton.eventos.view.VentanaPrincipal;
import javafx.application.Application;

/** Punto de entrada. No extiende Application para poder ejecutarse directo desde IntelliJ. */
public class Main {
    public static void main(String[] args) {
        Application.launch(VentanaPrincipal.class, args);
    }
}
