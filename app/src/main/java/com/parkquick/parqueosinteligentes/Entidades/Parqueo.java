package com.parkquick.parqueosinteligentes.Entidades;

public class Parqueo
{
  String tipo;
  int estado, idParkeo;
    Parqueo()
    {

    }
    public Parqueo(int estado, int idParkeo, String tipo) {
        this.estado = estado;
        this.idParkeo = idParkeo;
        this.tipo = tipo;

    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getIdParkeo() {
        return idParkeo;
    }

    public void setIdParkeo(int idParkeo) {
        this.idParkeo = idParkeo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
