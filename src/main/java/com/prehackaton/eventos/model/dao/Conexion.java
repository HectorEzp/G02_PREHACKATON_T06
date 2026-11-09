package com.prehackaton.eventos.model.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Proveedor de conexiones JDBC. Lee la configuración de db.properties.
 */
public final class Conexion {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Conexion.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró db.properties en resources.");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Conexion() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password"));
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
