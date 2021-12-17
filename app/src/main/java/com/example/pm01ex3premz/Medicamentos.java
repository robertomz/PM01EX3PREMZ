package com.example.pm01ex3premz;

public class Medicamentos {
    private int id;
    private String desc;
    private String cantidad;
    private String tiempo;
    private String periodo;
    private byte[] image;

    public Medicamentos(int id, String desc, String cantidad, String tiempo, String periodo, byte[] image) {
        this.id = id;
        this.desc = desc;
        this.cantidad = cantidad;
        this.tiempo = tiempo;
        this.periodo = periodo;
        this.image = image;
    }

    public Medicamentos() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
