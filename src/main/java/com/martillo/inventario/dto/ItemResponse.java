package com.martillo.inventario.dto;

import com.martillo.inventario.ItemInventario;

public class ItemResponse {

    private final Long id;
    private final String codigo;
    private final String nombre;
    private final int stock;
    private final int umbralCritico;
    private final String estado;

    public ItemResponse(ItemInventario item) {
        this.id = item.getId();
        this.codigo = item.getCodigo();
        this.nombre = item.getNombre();
        this.stock = item.getStock();
        this.umbralCritico = item.getUmbralCritico();
        this.estado = item.getEstado().name();
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getStock() { return stock; }
    public int getUmbralCritico() { return umbralCritico; }
    public String getEstado() { return estado; }
}
