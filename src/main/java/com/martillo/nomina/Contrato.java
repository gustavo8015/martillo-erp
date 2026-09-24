package com.martillo.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos minimos del contrato necesarios para liquidar.
 *
 * @param tipo           tipo de contrato laboral
 * @param ingreso        fecha de ingreso del trabajador
 * @param finPactado     fecha de vencimiento pactada; obligatoria en contratos
 *                       a termino fijo y por obra o labor, nula en indefinido
 * @param salarioMensual salario ordinario mensual
 */
public record Contrato(TipoContrato tipo, LocalDate ingreso, LocalDate finPactado, BigDecimal salarioMensual) {

    public Contrato {
        if (tipo == null) throw new IllegalArgumentException("El tipo de contrato es obligatorio");
        if (ingreso == null) throw new IllegalArgumentException("La fecha de ingreso es obligatoria");
        if (salarioMensual == null || salarioMensual.signum() <= 0) {
            throw new IllegalArgumentException("El salario mensual debe ser mayor a cero");
        }
        if (tipo != TipoContrato.INDEFINIDO && finPactado == null) {
            throw new IllegalArgumentException("El contrato " + tipo + " exige fecha de vencimiento pactada");
        }
    }
}
