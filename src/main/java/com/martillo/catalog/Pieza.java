package com.martillo.catalog;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Pieza: articulo del catalogo que puede ser incluido en una subasta.
 * SRP: solo conoce sus propios datos y el estado de catalogacion, nada de subastas ni comisiones.
 */
@Entity
@Table(name = "piezas")
public class Pieza {

    public enum EstadoPieza { DISPONIBLE, EN_SUBASTA, VENDIDA, RETIRADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPieza estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    protected Pieza() {
        // Constructor vacio requerido por JPA
    }

    public Pieza(String nombre, String descripcion, String categoria, BigDecimal precioReserva) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioReserva = precioReserva;
        this.estado = EstadoPieza.DISPONIBLE;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public BigDecimal getPrecioReserva() { return precioReserva; }
    public EstadoPieza getEstado() { return estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
