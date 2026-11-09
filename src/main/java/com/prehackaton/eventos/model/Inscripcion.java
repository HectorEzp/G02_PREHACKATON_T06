package com.prehackaton.eventos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Inscripcion {

    public static final List<String> ESTADOS = List.of("Inscrito", "Confirmado", "Cancelado");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int idInscripcion;
    private int idEvento;
    private int idParticipante;
    private LocalDateTime fechaInscripcion;
    private String estado;

    // Campos de solo lectura provenientes del JOIN
    private String eventoNombre;
    private String participanteNombre;
    private String participanteDni;

    public Inscripcion() {
    }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    public int getIdParticipante() { return idParticipante; }
    public void setIdParticipante(int idParticipante) { this.idParticipante = idParticipante; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEventoNombre() { return eventoNombre; }
    public void setEventoNombre(String eventoNombre) { this.eventoNombre = eventoNombre; }
    public String getParticipanteNombre() { return participanteNombre; }
    public void setParticipanteNombre(String participanteNombre) { this.participanteNombre = participanteNombre; }
    public String getParticipanteDni() { return participanteDni; }
    public void setParticipanteDni(String participanteDni) { this.participanteDni = participanteDni; }

    public String getFechaTexto() {
        return fechaInscripcion == null ? "" : fechaInscripcion.format(FMT);
    }
}

