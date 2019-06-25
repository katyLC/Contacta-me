package com.example.contactame.models;

import android.graphics.drawable.Drawable;

public class Categoria {
    private int image;
    private Drawable imageDrw;
    private String descripcionCorta;
    private String descripcion;
    private int image_bg;
    public Categoria() {
    }

    public int getImage() {
        return image;
    }

    public Drawable getImageDrw() {
        return imageDrw;
    }

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImage_bg() {
        return image_bg;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setImageDrw(Drawable imageDrw) {
        this.imageDrw = imageDrw;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImage_bg(int image_bg) {
        this.image_bg = image_bg;
    }
}
