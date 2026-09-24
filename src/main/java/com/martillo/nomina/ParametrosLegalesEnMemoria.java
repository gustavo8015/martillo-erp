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
 * Nota del Sprint 3: los valores de 2026 provienen de una fuente secundaria y
 * estan pendientes de contraste con el decreto oficial (impedimento declarado
 * en el informe del sprint).
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
