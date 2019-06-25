package com.example.contactame.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {

    private String uid;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    private boolean estado;
    private String experiencia;
    private String fotoUrl;
    private String servicio_uid;
    private String token;
    private String proveedor_uid;


    public Usuario() {
    }

    public Usuario(String uid, String nombres, String apellidos, String dni, String email, String telefono,
                   boolean estado,
                   String experiencia,String fotoUrl, String servicio_uid, String token, String proveedor_uid) {
        this.uid =  uid;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        this.experiencia = experiencia;
        this.fotoUrl = fotoUrl;
        this.servicio_uid = servicio_uid;
        this.token = token;
        this.proveedor_uid = proveedor_uid;
    }

    protected Usuario(Parcel in) {
        uid = in.readString();
        nombres = in.readString();
        apellidos = in.readString();
        dni = in.readString();
        email = in.readString();
        telefono = in.readString();
        estado = in.readByte() != 0;
        experiencia = in.readString();
        fotoUrl = in.readString();
        servicio_uid = in.readString();
        proveedor_uid = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isEstado() {
        return estado;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setFotoUrl(String foroUrl) {
        this.fotoUrl = foroUrl;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getServicio_uid() {
        return servicio_uid;
    }

    public void setServicio_uid(String servicio_uid) {
        this.servicio_uid = servicio_uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        dest.writeString(uid);
        dest.writeString(nombres);
        dest.writeString(apellidos);
        dest.writeString(dni);
        dest.writeString(email);
        dest.writeString(telefono);
        dest.writeByte((byte) (estado ? 1 : 0));
        dest.writeString(experiencia);
        dest.writeString(fotoUrl);
        dest.writeString(servicio_uid);
        dest.writeString(token);
        dest.writeString(proveedor_uid);
    }

}
