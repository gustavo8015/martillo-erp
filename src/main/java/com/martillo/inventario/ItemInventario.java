package com.martillo.inventario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Insumo de bodega, catalogacion o embalaje que consume la casa de subastas
 * (cajas, protecciones, etiquetas, catalogos impresos).
 */
@Entity
@Table(name = "items_inventario")
public class ItemInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private int stock;

    /** Cantidad por debajo de la cual hay que reponer. */
    @Column(nullable = false)
    private int umbralCritico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoStock estado = EstadoStock.NORMAL;

    protected ItemInventario() {
    }

    public ItemInventario(String codigo, String nombre, int stock, int umbralCritico) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.umbralCritico = umbralCritico;
        this.estado = stock < umbralCritico ? EstadoStock.CRITICO : EstadoStock.NORMAL;
    }

    /** Descuenta unidades por una salida de bodega. */
    public void descontar(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        if (cantidad > stock) throw new IllegalArgumentException("No hay stock suficiente de " + codigo);
        this.stock -= cantidad;
    }

    /** Suma unidades por una reposicion. */
    public void reponer(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        this.stock += cantidad;
    }

    public boolean bajoUmbral() {
        return stock < umbralCritico;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getStock() { return stock; }
    public int getUmbralCritico() { return umbralCritico; }
    public EstadoStock getEstado() { return estado; }
    public void setEstado(EstadoStock estado) { this.estado = estado; }
    public void setUmbralCritico(int umbralCritico) { this.umbralCritico = umbralCritico; }
}
