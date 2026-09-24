package com.martillo.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Pista de auditoria: quien intento que, cuando y con que resultado. */
@Entity
@Table(name = "auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String usuario;

    @Column(nullable = false, length = 60)
    private String evento;

    @Column(length = 240)
    private String detalle;

    @Column(nullable = false)
    private boolean permitido;

    @Column(nullable = false)
    private LocalDateTime momento = LocalDateTime.now();

    protected RegistroAuditoria() {
    }

    public RegistroAuditoria(String usuario, String evento, String detalle, boolean permitido) {
        this.usuario = usuario;
        this.evento = evento;
        this.detalle = detalle;
        this.permitido = permitido;
    }

    public Long getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getEvento() { return evento; }
    public String getDetalle() { return detalle; }
    public boolean isPermitido() { return permitido; }
    public LocalDateTime getMomento() { return momento; }
}
