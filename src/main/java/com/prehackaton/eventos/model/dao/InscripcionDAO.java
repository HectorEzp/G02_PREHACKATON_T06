package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.Inscripcion;
import java.sql.SQLException;

public interface InscripcionDAO extends CrudDAO<Inscripcion> {
    boolean existeInscripcion(int idEvento, int idParticipante, int excluirId) throws SQLException;
}
