package com.example.contactame.models;

public class Mensaje {
    private String mensaje;
    private long timestamp;
    private String deID;
    private String paraID;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeID() {
        return deID;
    }

    public void setDeID(String deID) {
        this.deID = deID;
    }

    public String getParaID() {
        return paraID;
    }

    public void setParaID(String paraID) {
        this.paraID = paraID;
    }
}
