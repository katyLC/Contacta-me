package com.example.contactame.models;

public class Favorito {
    private boolean servicio_uid;

    public boolean getServicio_uid() {
        return servicio_uid;
    }

    public void setServicio_uid(boolean servicio_uid) {
        this.servicio_uid = servicio_uid;
    }

    @Override
    public String toString() {
        return "Favorito{" +
            "servicio_uid=" + servicio_uid +
            '}';
    }
}
