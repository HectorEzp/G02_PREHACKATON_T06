package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.dao.Conexion;
import com.prehackaton.eventos.model.Evento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAOImpl implements EventoDAO {

    private static final String SELECT =
            "SELECT id_evento, nombre, descripcion, fecha, lugar, modalidad, estado FROM eventos";

    private Evento mapear(ResultSet rs) throws SQLException {
        Evento e = new Evento();
        e.setIdEvento(rs.getInt("id_evento"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        Timestamp ts = rs.getTimestamp("fecha");
        e.setFecha(ts == null ? null : ts.toLocalDateTime());
        e.setLugar(rs.getString("lugar"));
        e.setModalidad(rs.getString("modalidad"));
        e.setEstado(rs.getString("estado"));
        return e;
    }

    @Override
    public boolean insertar(Evento e) throws SQLException {
        String sql = "INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado) VALUES (?,?,?,?,?,?)";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(e.getFecha()));
            ps.setString(4, e.getLugar());
            ps.setString(5, e.getModalidad());
            ps.setString(6, e.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Evento e) throws SQLException {
        String sql = "UPDATE eventos SET nombre=?, descripcion=?, fecha=?, lugar=?, modalidad=?, estado=? WHERE id_evento=?";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(e.getFecha()));
            ps.setString(4, e.getLugar());
            ps.setString(5, e.getModalidad());
            ps.setString(6, e.getEstado());
            ps.setInt(7, e.getIdEvento());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM eventos WHERE id_evento=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Evento buscarPorId(int id) throws SQLException {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE id_evento=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Evento> listar() throws SQLException {
        return buscar("");
    }

    @Override
    public List<Evento> buscar(String texto) throws SQLException {
        String like = "%" + (texto == null ? "" : texto.trim()) + "%";
        String sql = SELECT + " WHERE nombre LIKE ? OR lugar LIKE ? OR modalidad LIKE ? OR estado LIKE ?"
                + " ORDER BY fecha DESC, nombre";
        List<Evento> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean existeDuplicado(String nombre, LocalDateTime fecha, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM eventos WHERE LOWER(nombre)=LOWER(?) AND fecha=? AND id_evento<>?";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setTimestamp(2, Timestamp.valueOf(fecha));
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
