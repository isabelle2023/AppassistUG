package com.aplication.assistug.model;

public class AlumnosHorario {

    String dia;
    int horaInicio;
    int horaFin;
    String materia;
    String semestre;
    String grupo;
    String uid;
    String uidAlumno;
    String uidCurso;

    public AlumnosHorario () {

    }

    public AlumnosHorario(String dia, int horaInicio, int horaFin, String materia, String semestre,String uid, String uidAlumno, String uidCurso, String grupo) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.materia = materia;
        this.semestre = semestre;
        this.uid = uid;
        this.uidAlumno = uidAlumno;
        this.uidCurso = uidCurso;
        this.grupo = grupo;
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

    public String getUidAlumno() {
        return uidAlumno;
    }

    public void setUidAlumno(String uidAlumno) {
        this.uidAlumno = uidAlumno;
    }

    public String getUidCurso() {
        return uidCurso;
    }

    public void setUidCurso(String uidCurso) {
        this.uidCurso = uidCurso;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}
