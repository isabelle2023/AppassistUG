package com.aplication.assistug.model;

public class RegistroMaestro {
    String uid;
    String nombre;
    String apellido;
    String cedula;
    String correo;
    String descripcion;
    String contraseña;
    String urlfoto;
    String tipo;
    String privilegio;
    String carrera;

    public RegistroMaestro (){

    }

    public RegistroMaestro(String uid,String nombre, String apellido, String cedula, String correo, String descripcion, String contraseña, String urlfoto, String tipo, String privilegio, String carrera) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.correo = correo;
        this.descripcion = descripcion;
        this.contraseña = contraseña;
        this.urlfoto = urlfoto;
        this.tipo = tipo;
        this.privilegio = privilegio;
        this.carrera = carrera;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setFoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(String privilegio) {
        this.privilegio = privilegio;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
}
