package com.martillo.nomina;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementacion de referencia con las vigencias conocidas.
 *
 * Fuente normativa de los valores de 2026, contrastada con el texto oficial:
 * Decreto 1469 del 29 de diciembre de 2025, que fija el salario minimo legal
 * mensual en $1.750.905, y Decreto 1470 de la misma fecha, que fija el auxilio
 * de transporte en $249.095 para quienes devengan hasta dos salarios minimos.
 * Ambos rigen desde el 1 de enero de 2026.
 *
 * El Decreto 1469 estuvo suspendido provisionalmente por el Consejo de Estado
 * entre el 13 de febrero y julio de 2026, cuando la suspension fue revocada.
 * Ese episodio es justamente el motivo de ADR-005: los valores se leen por
 * fecha de vigencia, de modo que una liquidacion ya practicada se reproduce
 * con los parametros que regian ese dia y no con los que rigen hoy.
 */
@Repository
public class ParametrosLegalesEnMemoria implements ParametrosLegalesRepository {

    private final List<ParametrosLegales> vigencias = new ArrayList<>(List.of(
            new ParametrosLegales(LocalDate.of(2024, 1, 1), new BigDecimal("1300000"), new BigDecimal("162000")),
            new ParametrosLegales(LocalDate.of(2025, 1, 1), new BigDecimal("1423500"), new BigDecimal("200000")),
            new ParametrosLegales(LocalDate.of(2026, 1, 1), new BigDecimal("1750905"), new BigDecimal("249095"))
    ));

    @Override
    public ParametrosLegales vigentesEn(LocalDate fecha) {
        return vigencias.stream()
                .filter(p -> !fecha.isBefore(p.vigenteDesde()))
                .max(Comparator.comparing(ParametrosLegales::vigenteDesde))
                .orElseThrow(() -> new IllegalStateException("No hay parametros legales vigentes en " + fecha));
    }
}
