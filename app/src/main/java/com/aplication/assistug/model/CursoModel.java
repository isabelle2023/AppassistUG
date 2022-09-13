package com.aplication.assistug.model;

public class CursoModel {

    String uid;
    String nombreCurso;
    String semestre;
    String maestrouid;
    String grupo;
    String carrera;
    String maestro;
    String fotocurso;
    long fechaIni, fechaFin;

    public CursoModel(){

    }

    public CursoModel(String uid, String nombreCurso, String semestre, String maestrouid, String maestro, String fotocurso,String grupo, String carrera, long fechaIni, long fechaFin) {
        this.uid = uid;
        this.nombreCurso = nombreCurso;
        this.semestre = semestre;
        this.maestrouid = maestrouid;
        this.maestro = maestro;
        this.fotocurso = fotocurso;
        this.grupo = grupo;
        this.carrera = carrera;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getMaestrouid() {
        return maestrouid;
    }

    public void setMaestrouid(String maestrouid) {
        this.maestrouid = maestrouid;
    }

    public String getMaestro() {
        return maestro;
    }

    public void setMaestro(String maestro) {
        this.maestro = maestro;
    }

    public String getFotocurso() {
        return fotocurso;
    }

    public void setFotocurso(String fotocurso) {
        this.fotocurso = fotocurso;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public long getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(long fechaIni) {
        this.fechaIni = fechaIni;
    }

    public long getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(long fechaFin) {
        this.fechaFin = fechaFin;
    }
}

