package com.martillo.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class MovimientoRequest {

    @NotBlank(message = "El codigo del item es obligatorio")
    private String codigo;

    @Positive(message = "La cantidad debe ser mayor a cero")
    private int cantidad;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
