package com.martillo.consignment.dto;

import com.martillo.consignment.Consignante;
import java.time.LocalDateTime;

/**
 * DTO de salida: lo que el API expone al cliente. Se construye a partir de la entidad,
 * pero nunca se devuelve la entidad directamente (buena practica de encapsulamiento).
 */
public class ConsignanteResponse {

    private Long id;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String email;
    private String direccion;
    private LocalDateTime fechaRegistro;

    public ConsignanteResponse(Consignante consignante) {
        this.id = consignante.getId();
        this.nombreCompleto = consignante.getNombreCompleto();
        this.tipoDocumento = consignante.getTipoDocumento();
        this.numeroDocumento = consignante.getNumeroDocumento();
        this.telefono = consignante.getTelefono();
        this.email = consignante.getEmail();
        this.direccion = consignante.getDireccion();
        this.fechaRegistro = consignante.getFechaRegistro();
    }

    public Long getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getDireccion() { return direccion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
