package com.example.parqueosinteligentes;

public class Usuario {
    String correo, nombre, tipo;
    Usuario()
    {

    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Usuario(String correo, String nombre, String tipo) {
        this.correo = correo;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }
}
