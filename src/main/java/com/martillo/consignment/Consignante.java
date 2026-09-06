package com.martillo.consignment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Consignante: persona o entidad que entrega piezas en consignacion.
 * Responsabilidad unica (SRP): representar el estado y las reglas propias de un consignante,
 * nada mas.
 */
@Entity
@Table(name = "consignantes")
public class Consignante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, length = 20)
    private String tipoDocumento;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroDocumento;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(length = 120)
    private String email;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    protected Consignante() {
        // Constructor vacio requerido por JPA
    }

    public Consignante(String nombreCompleto, String tipoDocumento, String numeroDocumento,
                        String telefono, String email, String direccion) {
        this.nombreCompleto = nombreCompleto;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getDireccion() { return direccion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public void actualizarDatosDeContacto(String telefono, String email, String direccion) {
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }
}
