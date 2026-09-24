package com.martillo.nomina.dto;

import com.martillo.nomina.MotivoTerminacion;
import com.martillo.nomina.TipoContrato;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LiquidacionRequest {

    @NotNull(message = "El tipo de contrato es obligatorio")
    private TipoContrato tipoContrato;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de retiro es obligatoria")
    private LocalDate fechaRetiro;

    /** Obligatoria en contratos a termino fijo y por obra o labor. */
    private LocalDate finPactado;

    @NotNull(message = "El salario mensual es obligatorio")
    @Positive(message = "El salario mensual debe ser mayor a cero")
    private BigDecimal salarioMensual;

    @NotNull(message = "El motivo de terminacion es obligatorio")
    private MotivoTerminacion motivo;

    public TipoContrato getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(TipoContrato tipoContrato) { this.tipoContrato = tipoContrato; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDate getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(LocalDate fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public LocalDate getFinPactado() { return finPactado; }
    public void setFinPactado(LocalDate finPactado) { this.finPactado = finPactado; }

    public BigDecimal getSalarioMensual() { return salarioMensual; }
    public void setSalarioMensual(BigDecimal salarioMensual) { this.salarioMensual = salarioMensual; }

    public MotivoTerminacion getMotivo() { return motivo; }
    public void setMotivo(MotivoTerminacion motivo) { this.motivo = motivo; }
}
