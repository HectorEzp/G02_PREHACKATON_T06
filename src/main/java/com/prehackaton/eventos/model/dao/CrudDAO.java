package com.prehackaton.eventos.model.dao;

import java.sql.SQLException;
import java.util.List;

/** Contrato CRUD genérico para la capa DAO. */
public interface CrudDAO<T> {
    boolean insertar(T entidad) throws SQLException;
    boolean actualizar(T entidad) throws SQLException;
    boolean eliminar(int id) throws SQLException;
    T buscarPorId(int id) throws SQLException;
    List<T> listar() throws SQLException;
    List<T> buscar(String texto) throws SQLException;
}
