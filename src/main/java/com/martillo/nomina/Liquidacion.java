package com.martillo.nomina;

import java.math.BigDecimal;

/**
 * Resultado del motor de liquidacion, concepto por concepto.
 *
 * @param diasTrabajados  dias del contrato con calendario comercial de 360
 * @param diasAnio        dias causados en el anio de retiro (cesantias)
 * @param diasSemestre    dias causados en el semestre de retiro (prima)
 * @param baseCesantias   salario mas auxilio de transporte cuando aplica
 * @param cesantias       CST, art. 249
 * @param intereses       Ley 52 de 1975
 * @param prima           CST, art. 306
 * @param vacaciones      CST, art. 186
 * @param indemnizacion   CST, art. 64; cero si no hay despido sin justa causa
 * @param total           suma de los conceptos anteriores
 */
public record Liquidacion(long diasTrabajados,
                          long diasAnio,
                          long diasSemestre,
                          BigDecimal baseCesantias,
                          BigDecimal cesantias,
                          BigDecimal intereses,
                          BigDecimal prima,
                          BigDecimal vacaciones,
                          BigDecimal indemnizacion,
                          BigDecimal total) {
}
