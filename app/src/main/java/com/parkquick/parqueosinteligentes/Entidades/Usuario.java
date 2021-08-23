package com.parkquick.parqueosinteligentes.Entidades;

public class Usuario {
    String correo, nombre, tipo, password;



    Usuario()
    {

    }
    public Usuario(String correo, String nombre, String tipo,String password) {
        this.correo = correo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.password=password;
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
    public void setPassword(String password) {
        this.password = password;
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
    public String getPassword() {
        return password;
    }
}
