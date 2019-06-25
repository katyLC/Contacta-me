package com.example.contactame.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.GeoPoint;

public class Servicio implements Parcelable {
    private String categoria;
    private String usuario_uid;
    private String servicio_uid;
    private String descripcion;
    private String pais;
    private String ciudad;
    private String horarioAtencion;
    private GeoPoint ubicacion;

    public Servicio() {
    }

    public Servicio(String categoria, String usuario_uid, String servicio_uid, String descripcion, String pais,
                    String ciudad,
                    String horarioAtencion,GeoPoint ubicacion) {
        this.categoria = categoria;
        this.usuario_uid = usuario_uid;
        this.servicio_uid = servicio_uid;
        this.descripcion = descripcion;
        this.pais = pais;
        this.ciudad = ciudad;
        this.horarioAtencion = horarioAtencion;
        this.ubicacion = ubicacion;
    }

    protected Servicio(Parcel in) {
        categoria = in.readString();
        usuario_uid = in.readString();
        servicio_uid = in.readString();
        descripcion = in.readString();
        pais = in.readString();
        ciudad = in.readString();
        horarioAtencion = in.readString();
    }

    public static final Creator<Servicio> CREATOR = new Creator<Servicio>() {
        @Override
        public Servicio createFromParcel(Parcel in) {
            return new Servicio(in);
        }

        @Override
        public Servicio[] newArray(int size) {
            return new Servicio[size];
        }
    };

    public String getUsuario_uid() {
        return usuario_uid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }
    public String getPais() {
        return pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getHorarioAtencion() {
        return horarioAtencion;
    }


    public void setUsuario_uid(String usuario_uid) {
        this.usuario_uid = usuario_uid;
    }

    public String getServicio_uid() {
        return servicio_uid;
    }

    public void setServicio_uid(String servicio_uid) {
        this.servicio_uid = servicio_uid;
    }

    public GeoPoint getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoria);
        dest.writeString(usuario_uid);
        dest.writeString(servicio_uid);
        dest.writeString(descripcion);
        dest.writeString(pais);
        dest.writeString(ciudad);
        dest.writeString(horarioAtencion);
    }
}
