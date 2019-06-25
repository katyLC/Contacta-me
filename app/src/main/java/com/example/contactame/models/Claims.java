package com.example.contactame.models;

public class Claims {

    private boolean cliente;
    private boolean proveedor;

    public Claims() {
    }

    public Claims(boolean cliente, boolean proveedor) {
        this.cliente = cliente;
        this.proveedor = proveedor;
    }

    public boolean isCliente() {
        return cliente;
    }

    public boolean isProveedor() {
        return proveedor;
    }
}
