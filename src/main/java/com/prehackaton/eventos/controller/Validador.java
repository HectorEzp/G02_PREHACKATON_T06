package com.prehackaton.eventos.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/** Validaciones de formato reutilizables por los controladores. */
public final class Validador {

    private static final Pattern DNI = Pattern.compile("^\\d{8}$");
    private static final Pattern TELEFONO = Pattern.compile("^\\d{9}$");
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern NOMBRE = Pattern.compile("^[\\p{L}][\\p{L} .'-]*$");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Validador() {
    }

    public static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean esDni(String s) {
        return DNI.matcher(limpiar(s)).matches();
    }

    public static boolean esTelefono(String s) {
        return TELEFONO.matcher(limpiar(s)).matches();
    }

    public static boolean esCorreo(String s) {
        return CORREO.matcher(limpiar(s)).matches();
    }

    public static boolean esNombre(String s) {
        return NOMBRE.matcher(limpiar(s)).matches();
    }

    /** @return la hora o null si el formato no es HH:mm. */
    public static LocalTime parseHora(String s) {
        try {
            return LocalTime.parse(limpiar(s), HORA);
        } catch (Exception e) {
            return null;
        }
    }
}
