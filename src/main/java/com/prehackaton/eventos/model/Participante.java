package com.prehackaton.eventos.model;

public class Participante {

    private int idParticipante;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;
    private String institucion;

    public Participante() {
    }

    public int getIdParticipante() { return idParticipante; }
    public void setIdParticipante(int idParticipante) { this.idParticipante = idParticipante; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public String getNombreCompleto() {
        return apellidos + ", " + nombres;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " - " + dni;
    }
}
