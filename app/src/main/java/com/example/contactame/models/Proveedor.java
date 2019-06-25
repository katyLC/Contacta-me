package com.example.contactame.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.firebase.firestore.GeoPoint;

public class Proveedor implements Parcelable {

    private String servicio_uid;
    private String usuario_uid;
    private String proveedor_uid;
    private String proveedor_nombre;
    private String proveedor_dni;
    private String proveedor_email;
    private String servicio_ciudad;
    private String servicio_categoria;
    private String servicio_descripcion;
    private String servicio_horarioAtencion;
    private float proveedor_promedioCalificacion;
    private int proveedor_cantidadCalificacion;
    private String proveedor_foto;
    private GeoPoint servicio_ubicacion;

    public Proveedor() {
    }

    protected Proveedor(Parcel in) {
        servicio_uid = in.readString();
        usuario_uid = in.readString();
        proveedor_uid = in.readString();
        proveedor_nombre = in.readString();
        proveedor_dni = in.readString();
        proveedor_email = in.readString();
        servicio_ciudad = in.readString();
        servicio_categoria = in.readString();
        servicio_descripcion = in.readString();
        servicio_horarioAtencion = in.readString();
        proveedor_promedioCalificacion = in.readFloat();
        proveedor_cantidadCalificacion = in.readInt();
        proveedor_foto = in.readString();
    }

    public static final Creator<Proveedor> CREATOR = new Creator<Proveedor>() {
        @Override
        public Proveedor createFromParcel(Parcel in) {
            return new Proveedor(in);
        }

        @Override
        public Proveedor[] newArray(int size) {
            return new Proveedor[size];
        }
    };

    public String getServicio_uid() {
        return servicio_uid;
    }

    public void setServicio_uid(String servicio_uid) {
        this.servicio_uid = servicio_uid;
    }

    public String getUsuario_uid() {
        return usuario_uid;
    }

    public void setUsuario_uid(String usuario_uid) {
        this.usuario_uid = usuario_uid;
    }

    public String getProveedor_uid() {
        return proveedor_uid;
    }

    public void setProveedor_uid(String proveedor_uid) {
        this.proveedor_uid = proveedor_uid;
    }

    public String getProveedor_nombre() {
        return proveedor_nombre;
    }

    public void setProveedor_nombre(String proveedor_nombre) {
        this.proveedor_nombre = proveedor_nombre;
    }

    public String getProveedor_dni() {
        return proveedor_dni;
    }

    public void setProveedor_dni(String proveedor_dni) {
        this.proveedor_dni = proveedor_dni;
    }

    public String getProveedor_email() {
        return proveedor_email;
    }

    public void setProveedor_email(String proveedor_email) {
        this.proveedor_email = proveedor_email;
    }

    public String getServicio_ciudad() {
        return servicio_ciudad;
    }

    public void setServicio_ciudad(String servicio_ciudad) {
        this.servicio_ciudad = servicio_ciudad;
    }

    public String getServicio_categoria() {
        return servicio_categoria;
    }

    public void setServicio_categoria(String servicio_categoria) {
        this.servicio_categoria = servicio_categoria;
    }

    public String getServicio_descripcion() {
        return servicio_descripcion;
    }

    public void setServicio_descripcion(String servicio_descripcion) {
        this.servicio_descripcion = servicio_descripcion;
    }

    public String getServicio_horarioAtencion() {
        return servicio_horarioAtencion;
    }

    public void setServicio_horarioAtencion(String servicio_horarioAtencion) {
        this.servicio_horarioAtencion = servicio_horarioAtencion;
    }

    public GeoPoint getServicio_ubicacion() {
        return servicio_ubicacion;
    }

    public void setServicio_ubicacion(GeoPoint servicio_ubicacion) {
        this.servicio_ubicacion = servicio_ubicacion;
    }

    public float getProveedor_promedioCalificacion() {
        return proveedor_promedioCalificacion;
    }

    public void setProveedor_promedioCalificacion(float proveedor_promedioCalificacion) {
        this.proveedor_promedioCalificacion = proveedor_promedioCalificacion;
    }

    public int getProveedor_cantidadCalificacion() {
        return proveedor_cantidadCalificacion;
    }

    public void setProveedor_cantidadCalificacion(int proveedor_cantidadCalificacion) {
        this.proveedor_cantidadCalificacion = proveedor_cantidadCalificacion;
    }

    public String getProveedor_foto() {
        return proveedor_foto;
    }

    public void setProveedor_foto(String proveedor_foto) {
        this.proveedor_foto = proveedor_foto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(servicio_uid);
        dest.writeString(usuario_uid);
        dest.writeString(proveedor_uid);
        dest.writeString(proveedor_nombre);
        dest.writeString(proveedor_dni);
        dest.writeString(proveedor_email);
        dest.writeString(servicio_ciudad);
        dest.writeString(servicio_categoria);
        dest.writeString(servicio_descripcion);
        dest.writeString(servicio_horarioAtencion);
        dest.writeFloat(proveedor_promedioCalificacion);
        dest.writeInt(proveedor_cantidadCalificacion);
        dest.writeString(proveedor_foto);
    }
}
