package com.martillo.catalog.dto;

import com.martillo.catalog.Pieza;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PiezaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precioReserva;
    private String estado;
    private LocalDateTime fechaRegistro;

    public PiezaResponse(Pieza pieza) {
        this.id = pieza.getId();
        this.nombre = pieza.getNombre();
        this.descripcion = pieza.getDescripcion();
        this.categoria = pieza.getCategoria();
        this.precioReserva = pieza.getPrecioReserva();
        this.estado = pieza.getEstado().name();
        this.fechaRegistro = pieza.getFechaRegistro();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public BigDecimal getPrecioReserva() { return precioReserva; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
