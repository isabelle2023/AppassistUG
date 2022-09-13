package com.aplication.assistug.model;

public class AlumnoAsistencia {
    String uidAlumno, status, nombre, cedula, uidhorario;
    long horaLlegada;
    long fecha;

    AlumnoAsistencia(){
    }

    public AlumnoAsistencia(String uidAlumno, String status, long horaLlegada, long fecha, String uidhorario) {
        this.uidAlumno = uidAlumno;
        this.status = status;
        this.horaLlegada = horaLlegada;
        this.fecha = fecha;
        this.uidhorario = uidhorario;
    }

    public String getUidhorario() {
        return uidhorario;
    }

    public void setUidhorario(String uidhorario) {
        this.uidhorario = uidhorario;
    }

    public String getCedula() {
        return cedula;
    }
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUidAlumno() {
        return uidAlumno;
    }

    public void setUidAlumno(String uidAlumno) {
        this.uidAlumno = uidAlumno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(long horallegada) {
        this.horaLlegada = horallegada;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
