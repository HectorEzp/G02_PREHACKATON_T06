package com.prehackaton.eventos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Evento {

    public static final List<String> MODALIDADES = List.of("Presencial", "Virtual", "Híbrido");
    public static final List<String> ESTADOS = List.of("Programado", "En curso", "Finalizado", "Cancelado");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int idEvento;
    private String nombre;
    private String descripcion;
    private LocalDateTime fecha;
    private String lugar;
    private String modalidad;
    private String estado;

    public Evento() {
    }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /** Usado por la columna de la TableView. */
    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(FMT);
    }

    @Override
    public String toString() {
        return fecha == null ? nombre : nombre + " (" + getFechaTexto() + ")";
    }
}
