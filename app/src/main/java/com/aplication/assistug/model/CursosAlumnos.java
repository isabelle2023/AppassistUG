package com.aplication.assistug.model;

public class CursosAlumnos{
    String nombre, uidalumno, uidcurso, cedula;


    CursosAlumnos(){
    }

    public CursosAlumnos(String nombre, String uidalumno, String uidcurso, String cedula) {
        this.nombre = nombre;
        this.uidalumno = uidalumno;
        this.uidcurso = uidcurso;
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUidalumno() {
        return uidalumno;
    }

    public void setUidalumno(String uidalumno) {
        this.uidalumno = uidalumno;
    }

    public String getUidcurso() {
        return uidcurso;
    }

    public void setUidcurso(String uidcurso) {
        this.uidcurso = uidcurso;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
}

