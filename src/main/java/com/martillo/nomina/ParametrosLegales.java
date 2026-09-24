package com.martillo.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Valores fijados por norma para un periodo. ADR-005: no son constantes del
 * codigo sino parametros con fecha de vigencia, para que la liquidacion de un
 * periodo cerrado se pueda reproducir.
 *
 * @param vigenteDesde      primer dia en que rigen estos valores
 * @param smmlv             salario minimo mensual legal vigente
 * @param auxilioTransporte auxilio de transporte mensual
 */
public record ParametrosLegales(LocalDate vigenteDesde, BigDecimal smmlv, BigDecimal auxilioTransporte) {
}
