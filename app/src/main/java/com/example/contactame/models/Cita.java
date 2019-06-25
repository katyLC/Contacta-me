package com.example.contactame.models;

import java.util.Date;

public class Cita {
    private String citaID;
    private Date fecha;
    private Date hora;
    private Date notificacion;
    private String observacion;
    private String deID;
    private String paraID;
    private String estado;
    private String qrUrl;
    private String usuario_nombre;
    private String proveedor_uid;

    public Cita() {
    }

    public String getCitaID() {
        return citaID;
    }

    public Date getFecha() {
        return fecha;
    }

    public Date getHora() {
        return hora;
    }

    public Date getNotificacion() {
        return notificacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public void setNotificacion(Date notificacion) {
        this.notificacion = notificacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getDeID() {
        return deID;
    }

    public String getParaID() {
        return paraID;
    }

    public String getEstado() {
        return estado;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public String getUsuario_nombre() {
        return usuario_nombre;
    }

    public void setCitaID(String citaID) {
        this.citaID = citaID;
    }

    public void setDeID(String deID) {
        this.deID = deID;
    }

    public void setParaID(String paraID) {
        this.paraID = paraID;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public void setUsuario_nombre(String usuario_nombre) {
        this.usuario_nombre = usuario_nombre;
    }

    public String getProveedor_uid() {
        return proveedor_uid;
    }

    public void setProveedor_uid(String proveedor_uid) {
        this.proveedor_uid = proveedor_uid;
    }
}
