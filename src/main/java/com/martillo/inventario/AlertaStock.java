package com.martillo.inventario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Alerta generada cuando un item cruza su umbral critico. */
@Entity
@Table(name = "alertas_stock")
public class AlertaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String codigoItem;

    @Column(nullable = false, length = 240)
    private String mensaje;

    @Column(nullable = false)
    private int stockAlGenerar;

    @Column(nullable = false)
    private LocalDateTime momento = LocalDateTime.now();

    @Column(nullable = false)
    private boolean atendida = false;

    protected AlertaStock() {
    }

    public AlertaStock(String codigoItem, String mensaje, int stockAlGenerar) {
        this.codigoItem = codigoItem;
        this.mensaje = mensaje;
        this.stockAlGenerar = stockAlGenerar;
    }

    public Long getId() { return id; }
    public String getCodigoItem() { return codigoItem; }
    public String getMensaje() { return mensaje; }
    public int getStockAlGenerar() { return stockAlGenerar; }
    public LocalDateTime getMomento() { return momento; }
    public boolean isAtendida() { return atendida; }
    public void setAtendida(boolean atendida) { this.atendida = atendida; }
}
