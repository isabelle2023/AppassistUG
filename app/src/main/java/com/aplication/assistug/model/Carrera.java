package com.aplication.assistug.model;

public class Carrera {
    Integer id;
    String nomCarrera;
    String carreraCustom;

    public Carrera (){

    }

    public Carrera(Integer id, String nomCarrera) {
        this.id = id;
        this.nomCarrera = nomCarrera;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomCarrera() {
        return nomCarrera;
    }

    public void setNomCarrera(String nomCarrera) {
        this.nomCarrera = nomCarrera;
    }

    @Override
    public String toString() {
        this.carreraCustom = this.carreraCustom = nomCarrera;
        return carreraCustom;
    }
}
