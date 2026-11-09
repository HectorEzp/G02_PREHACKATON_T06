package com.prehackaton.eventos.view;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

/** Utilidades de UI para formularios. */
public final class Formularios {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formularios() {
    }

    /** DatePicker personalizado: formato dd/MM/yyyy, edición manual y fines de semana resaltados. */
    public static void configurarDatePicker(DatePicker dp) {
        dp.setPromptText("dd/mm/aaaa");
        dp.setEditable(true);
        dp.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d == null ? "" : FECHA.format(d);
            }

            @Override
            public LocalDate fromString(String s) {
                if (s == null || s.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(s.trim(), FECHA);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
        dp.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("");
                if (!empty && item != null
                        && (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    setStyle("-fx-background-color:#FEF3C7;");
                }
            }
        });
    }

    /** Lee la fecha considerando también el texto escrito a mano. @return null si es inválida o está vacía. */
    public static LocalDate leerFecha(DatePicker dp) {
        LocalDate fecha = dp.getConverter().fromString(dp.getEditor().getText());
        dp.setValue(fecha);
        return fecha;
    }
}
