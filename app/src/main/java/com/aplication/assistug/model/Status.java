package com.aplication.assistug.model;

public class Status {

    Integer id;
    String nomStatus;

    public Status(Integer id, String nomStatus) {
        this.id = id;
        this.nomStatus = nomStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomStatus() {
        return nomStatus;
    }

    public void setNomStatus(String nomStatus) {
        this.nomStatus = nomStatus;
    }
}
