package com.aplication.assistug.model;

public class HorarioModel {
    long fecha;
    String materia;
    String semestre;
    String dia;
    int horaInicio;
    int horaFin;
    String uid;

    public HorarioModel(){

    }

    public HorarioModel(String materia, String semestre, String dia, int horaInicio, int horaFin, String uid, long fecha) {
        this.materia = materia;
        this.semestre = semestre;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.uid = uid;
        this.fecha = fecha;
    }




    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(int horaInicio) {
        this.horaInicio = horaInicio;
    }

    public int getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(int horaFin) {
        this.horaFin = horaFin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
