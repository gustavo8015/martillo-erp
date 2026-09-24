package com.martillo.nomina.dto;

import com.martillo.nomina.Liquidacion;

import java.math.BigDecimal;

/** Vista de la liquidacion para la interfaz; separada del dominio (SRP). */
public class LiquidacionResponse {

    private long diasTrabajados;
    private long diasAnio;
    private long diasSemestre;
    private BigDecimal baseCesantias;
    private BigDecimal cesantias;
    private BigDecimal interesesCesantias;
    private BigDecimal prima;
    private BigDecimal vacaciones;
    private BigDecimal indemnizacion;
    private BigDecimal total;

    public static LiquidacionResponse de(Liquidacion l) {
        LiquidacionResponse r = new LiquidacionResponse();
        r.diasTrabajados = l.diasTrabajados();
        r.diasAnio = l.diasAnio();
        r.diasSemestre = l.diasSemestre();
        r.baseCesantias = l.baseCesantias();
        r.cesantias = l.cesantias();
        r.interesesCesantias = l.intereses();
        r.prima = l.prima();
        r.vacaciones = l.vacaciones();
        r.indemnizacion = l.indemnizacion();
        r.total = l.total();
        return r;
    }

    public long getDiasTrabajados() { return diasTrabajados; }
    public long getDiasAnio() { return diasAnio; }
    public long getDiasSemestre() { return diasSemestre; }
    public BigDecimal getBaseCesantias() { return baseCesantias; }
    public BigDecimal getCesantias() { return cesantias; }
    public BigDecimal getInteresesCesantias() { return interesesCesantias; }
    public BigDecimal getPrima() { return prima; }
    public BigDecimal getVacaciones() { return vacaciones; }
    public BigDecimal getIndemnizacion() { return indemnizacion; }
    public BigDecimal getTotal() { return total; }
}
