package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.dao.Conexion;
import com.prehackaton.eventos.model.Participante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteDAOImpl implements ParticipanteDAO {

    private static final String SELECT =
            "SELECT id_participante, nombres, apellidos, dni, correo, telefono, institucion FROM participantes";

    private Participante mapear(ResultSet rs) throws SQLException {
        Participante p = new Participante();
        p.setIdParticipante(rs.getInt("id_participante"));
        p.setNombres(rs.getString("nombres"));
        p.setApellidos(rs.getString("apellidos"));
        p.setDni(rs.getString("dni"));
        p.setCorreo(rs.getString("correo"));
        p.setTelefono(rs.getString("telefono"));
        p.setInstitucion(rs.getString("institucion"));
        return p;
    }

    @Override
    public boolean insertar(Participante p) throws SQLException {
        String sql = "INSERT INTO participantes (nombres, apellidos, dni, correo, telefono, institucion) VALUES (?,?,?,?,?,?)";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getDni());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getInstitucion());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean actualizar(Participante p) throws SQLException {
        String sql = "UPDATE participantes SET nombres=?, apellidos=?, dni=?, correo=?, telefono=?, institucion=? WHERE id_participante=?";
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getDni());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getInstitucion());
            ps.setInt(7, p.getIdParticipante());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM participantes WHERE id_participante=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Participante buscarPorId(int id) throws SQLException {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE id_participante=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Participante> listar() throws SQLException {
        return buscar("");
    }

    @Override
    public List<Participante> buscar(String texto) throws SQLException {
        String like = "%" + (texto == null ? "" : texto.trim()) + "%";
        String sql = SELECT + " WHERE nombres LIKE ? OR apellidos LIKE ? OR dni LIKE ? OR correo LIKE ? OR institucion LIKE ?"
                + " ORDER BY apellidos, nombres";
        List<Participante> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
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
    public boolean existeDni(String dni, int excluirId) throws SQLException {
        return existe("SELECT COUNT(*) FROM participantes WHERE dni=? AND id_participante<>?", dni, excluirId);
    }

    @Override
    public boolean existeCorreo(String correo, int excluirId) throws SQLException {
        return existe("SELECT COUNT(*) FROM participantes WHERE LOWER(correo)=LOWER(?) AND id_participante<>?", correo, excluirId);
    }

    private boolean existe(String sql, String valor, int excluirId) throws SQLException {
        try (Connection cn = Conexion.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public List<String> listarInstituciones() throws SQLException {
        List<String> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT institucion FROM participantes ORDER BY institucion")) {
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        }
        return lista;
    }
}
