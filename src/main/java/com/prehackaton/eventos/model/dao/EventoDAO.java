package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.Evento;
import java.sql.SQLException;
import java.time.LocalDateTime;

public interface EventoDAO extends CrudDAO<Evento> {
    /** Verifica si existe otro evento con el mismo nombre y fecha (excluye el id indicado). */
    boolean existeDuplicado(String nombre, LocalDateTime fecha, int excluirId) throws SQLException;
}
