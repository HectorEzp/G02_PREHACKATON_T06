package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.Participante;
import java.sql.SQLException;
import java.util.List;

public interface ParticipanteDAO extends CrudDAO<Participante> {
    boolean existeDni(String dni, int excluirId) throws SQLException;
    boolean existeCorreo(String correo, int excluirId) throws SQLException;
    List<String> listarInstituciones() throws SQLException;
}
