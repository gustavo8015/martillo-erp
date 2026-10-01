package com.martillo.nomina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * Eficiencia de desempeno (ISO/IEC 25010): el motor de liquidacion procesa
 * 10.000 liquidaciones en menos de dos segundos y todas dan el mismo total,
 * lo que tambien comprueba que el calculo no acumula estado entre llamadas.
 */
class CalculadoraLiquidacionRendimientoTest {

    @Test
    @DisplayName("CP-PERF.1 10.000 liquidaciones en menos de 2 segundos con resultado estable")
    void diezMilLiquidaciones() {
        CalculadoraLiquidacion calculadora = new CalculadoraLiquidacion(new ParametrosLegalesEnMemoria());
        Contrato contrato = new Contrato(TipoContrato.INDEFINIDO,
                LocalDate.of(2026, 1, 1), null, new BigDecimal("2500000"));

        assertTimeout(Duration.ofSeconds(2), () -> {
            for (int i = 0; i < 10_000; i++) {
                Liquidacion l = calculadora.liquidar(contrato, LocalDate.of(2026, 9, 30),
                        MotivoTerminacion.SIN_JUSTA_CAUSA);
                assertEquals(new BigDecimal("6372159"), l.total());
            }
        });
    }
}
