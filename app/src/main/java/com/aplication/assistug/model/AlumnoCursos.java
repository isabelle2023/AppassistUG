package com.aplication.assistug.model;

public class AlumnoCursos {

    String uidAlumno, uidCurso;

    public AlumnoCursos() {
    }

    public AlumnoCursos(String uidAlumno, String uidCurso) {
        this.uidAlumno = uidAlumno;
        this.uidCurso = uidCurso;
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
}
