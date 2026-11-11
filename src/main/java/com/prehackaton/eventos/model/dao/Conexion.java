package com.prehackaton.eventos.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proveedor de conexiones JDBC hacia MySQL (Docker local).
 */
public final class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/db_eventos"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "12345678";

    private Conexion() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    /** @return null si la conexión es correcta; de lo contrario, el mensaje de error. */
    public static String probar() {
        try (Connection cn = getConnection()) {
            return cn.isValid(3) ? null : "La conexión no es válida.";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
}