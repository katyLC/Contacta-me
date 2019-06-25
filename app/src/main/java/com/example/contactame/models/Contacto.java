package com.example.contactame.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contacto implements Parcelable {

    private String uuid;
    private String usuario_nombre;
    private String ultimoMensaje;
    private long timeStamp;
    private String fotoUrl;
    private String proveedor_uid;

    public Contacto() {
    }

    protected Contacto(Parcel in) {
        uuid = in.readString();
        usuario_nombre = in.readString();
        ultimoMensaje = in.readString();
        timeStamp = in.readLong();
        fotoUrl = in.readString();
        proveedor_uid = in.readString();
    }

    public static final Creator<Contacto> CREATOR = new Creator<Contacto>() {
        @Override
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        @Override
        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsuario_nombre() {
        return usuario_nombre;
    }

    public void setUsuario_nombre(String usuario_nombre) {
        this.usuario_nombre = usuario_nombre;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getProveedor_uid() {
        return proveedor_uid;
    }

    public void setProveedor_uid(String proveedor_uid) {
        this.proveedor_uid = proveedor_uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(usuario_nombre);
        dest.writeString(ultimoMensaje);
        dest.writeLong(timeStamp);
        dest.writeString(fotoUrl);
        dest.writeString(proveedor_uid);
    }


}