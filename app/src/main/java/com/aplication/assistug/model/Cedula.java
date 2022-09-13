package com.aplication.assistug.model;

public class Cedula {

    String uid, cedula;

    public Cedula() {
    }

    public Cedula(String uid, String cedula) {
        this.uid = uid;
        this.cedula = cedula;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
}
