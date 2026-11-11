package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.dao.Conexion;
import com.prehackaton.eventos.model.Inscripcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAOImpl implements InscripcionDAO {

    private static final String SELECT =
            "SELECT i.id_inscripcion, i.id_evento, i.id_participante, i.fecha_inscripcion, i.estado, "
          + "e.nombre AS evento, CONCAT(p.apellidos, ', ', p.nombres) AS participante, p.dni "
          + "FROM inscripciones i "
          + "INNER JOIN eventos e ON e.id_evento = i.id_evento "
          + "INNER JOIN participantes p ON p.id_participante = i.id_participante";

    private Inscripcion mapear(ResultSet rs) throws SQLException {
        Inscripcion i = new Inscripcion();
        i.setIdInscripcion(rs.getInt("id_inscripcion"));
        i.setIdEvento(rs.getInt("id_evento"));
        i.setIdParticipante(rs.getInt("id_participante"));
        Timestamp ts = rs.getTimestamp("fecha_inscripcion");
        i.setFechaInscripcion(ts == null ? null : ts.toLocalDateTime());
        i.setEstado(rs.getString("estado"));
        i.setEventoNombre(rs.getString("evento"));
        i.setParticipanteNombre(rs.getString("participante"));
        i.setParticipanteDni(rs.getString("dni"));
        return i;
    }

    @Override
    public boolean insertar(Inscripcion i) throws SQLException {
        String sql = "INSERT INTO inscripciones (id_evento, id_participante, fecha_inscripcion, estado) VALUES (?,?,?,?)";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, i.getIdEvento());
            ps.setInt(2, i.getIdParticipante());
            ps.setTimestamp(3, Timestamp.valueOf(i.getFechaInscripcion()));
            ps.setString(4, i.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Inscripcion i) throws SQLException {
        String sql = "UPDATE inscripciones SET id_evento=?, id_participante=?, fecha_inscripcion=?, estado=? WHERE id_inscripcion=?";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, i.getIdEvento());
            ps.setInt(2, i.getIdParticipante());
            ps.setTimestamp(3, Timestamp.valueOf(i.getFechaInscripcion()));
            ps.setString(4, i.getEstado());
            ps.setInt(5, i.getIdInscripcion());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM inscripciones WHERE id_inscripcion=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Inscripcion buscarPorId(int id) throws SQLException {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE i.id_inscripcion=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Inscripcion> listar() throws SQLException {
        return buscar("");
    }

    @Override
    public List<Inscripcion> buscar(String texto) throws SQLException {
        String like = "%" + (texto == null ? "" : texto.trim()) + "%";
        String sql = SELECT + " WHERE e.nombre LIKE ? OR p.nombres LIKE ? OR p.apellidos LIKE ? OR p.dni LIKE ? OR i.estado LIKE ?"
                + " ORDER BY i.fecha_inscripcion DESC";
        List<Inscripcion> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int k = 1; k <= 5; k++) {
                ps.setString(k, like);
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
    public boolean existeInscripcion(int idEvento, int idParticipante, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE id_evento=? AND id_participante=? AND id_inscripcion<>?";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ps.setInt(2, idParticipante);
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
