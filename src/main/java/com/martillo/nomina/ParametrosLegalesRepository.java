package com.martillo.nomina;

import java.time.LocalDate;

/**
 * Puerto de salida del motor de liquidacion (arquitectura hexagonal): la
 * calculadora consulta los parametros legales por esta interfaz y no conoce
 * de donde salen.
 */
public interface ParametrosLegalesRepository {

    /** Parametros vigentes en la fecha indicada. */
    ParametrosLegales vigentesEn(LocalDate fecha);
}
