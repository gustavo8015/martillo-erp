package com.martillo.nomina;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verificacion del motor de liquidacion (HU-07) contra el caso de referencia
 * del informe del Sprint 3, calculado de forma independiente.
 *
 * Caso: salario de 2.500.000, ingreso el 1 de enero de 2026 y retiro el 30 de
 * septiembre de 2026. El salario no supera dos salarios minimos, de modo que
 * hay auxilio de transporte y la base de cesantias y prima es de 2.749.095.
 */
class CalculadoraLiquidacionTest {

    private CalculadoraLiquidacion calculadora;

    private static final LocalDate INGRESO = LocalDate.of(2026, 1, 1);
    private static final LocalDate RETIRO = LocalDate.of(2026, 9, 30);
    private static final BigDecimal SALARIO = new BigDecimal("2500000");

    @BeforeEach
    void prepararMotor() {
        calculadora = new CalculadoraLiquidacion(new ParametrosLegalesEnMemoria());
    }

    @Test
    @DisplayName("Los dias se cuentan con el calendario comercial de 360 dias")
    void contarDiasConCalendarioComercial() {
        assertEquals(270, CalculadoraLiquidacion.dias360(INGRESO, RETIRO));
        // El ultimo dia de febrero cuenta como dia 30, de modo que enero y febrero suman 60.
        assertEquals(60, CalculadoraLiquidacion.dias360(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 28)));
    }

    @Test
    @DisplayName("Contrato indefinido: 30 dias de indemnizacion y total de 6.372.159")
    void liquidarContratoIndefinido() {
        Liquidacion l = calculadora.liquidar(
                new Contrato(TipoContrato.INDEFINIDO, INGRESO, null, SALARIO),
                RETIRO, MotivoTerminacion.SIN_JUSTA_CAUSA);

        assertEquals(new BigDecimal("2749095"), l.baseCesantias());
        assertEquals(new BigDecimal("2061821"), l.cesantias());
        assertEquals(new BigDecimal("185564"), l.intereses());
        assertEquals(new BigDecimal("687274"), l.prima());
        assertEquals(new BigDecimal("937500"), l.vacaciones());
        assertEquals(new BigDecimal("2500000"), l.indemnizacion());
        assertEquals(new BigDecimal("6372159"), l.total());
    }

    @Test
    @DisplayName("Contrato a termino fijo: los salarios que faltan hasta el vencimiento")
    void liquidarContratoFijo() {
        Liquidacion l = calculadora.liquidar(
                new Contrato(TipoContrato.FIJO, INGRESO, LocalDate.of(2026, 12, 31), SALARIO),
                RETIRO, MotivoTerminacion.SIN_JUSTA_CAUSA);

        assertEquals(new BigDecimal("7500000"), l.indemnizacion());
        assertEquals(new BigDecimal("11372159"), l.total());
    }

    @Test
    @DisplayName("Contrato por obra o labor: los salarios que faltan, con minimo de 15 dias")
    void liquidarContratoObraLabor() {
        Liquidacion l = calculadora.liquidar(
                new Contrato(TipoContrato.OBRA_LABOR, INGRESO, LocalDate.of(2026, 11, 15), SALARIO),
                RETIRO, MotivoTerminacion.SIN_JUSTA_CAUSA);

        assertEquals(new BigDecimal("3750000"), l.indemnizacion());
        assertEquals(new BigDecimal("7622159"), l.total());
    }

    @Test
    @DisplayName("Sin despido injustificado no hay indemnizacion y el total es 3.872.159")
    void liquidarRenunciaSinIndemnizacion() {
        for (MotivoTerminacion motivo : new MotivoTerminacion[]{
                MotivoTerminacion.RENUNCIA, MotivoTerminacion.JUSTA_CAUSA}) {
            Liquidacion l = calculadora.liquidar(
                    new Contrato(TipoContrato.INDEFINIDO, INGRESO, null, SALARIO),
                    RETIRO, motivo);
            assertEquals(BigDecimal.ZERO, l.indemnizacion());
            assertEquals(new BigDecimal("3872159"), l.total());
        }
    }

    @Test
    @DisplayName("Caso de borde: salario de 10 salarios minimos o mas usa 20 y 15 dias")
    void liquidarSalarioAlto() {
        Liquidacion l = calculadora.liquidar(
                new Contrato(TipoContrato.INDEFINIDO, LocalDate.of(2023, 5, 15), null, new BigDecimal("20000000")),
                RETIRO, MotivoTerminacion.SIN_JUSTA_CAUSA);

        assertEquals(1216, l.diasTrabajados());
        // 20 dias del primer anio mas 15 por cada anio adicional proporcional: 55,67 dias.
        assertEquals(new BigDecimal("37111111"), l.indemnizacion());
    }

    @Test
    @DisplayName("El salario alto no recibe auxilio de transporte en la base")
    void salarioAltoSinAuxilio() {
        Liquidacion l = calculadora.liquidar(
                new Contrato(TipoContrato.INDEFINIDO, INGRESO, null, new BigDecimal("6000000")),
                RETIRO, MotivoTerminacion.RENUNCIA);

        assertEquals(new BigDecimal("6000000"), l.baseCesantias());
    }

    @Test
    @DisplayName("ADR-005: la liquidacion de un periodo cerrado se reproduce con su vigencia")
    void reproducirPeriodoCerrado() {
        Liquidacion anterior = calculadora.liquidar(
                new Contrato(TipoContrato.INDEFINIDO, LocalDate.of(2025, 1, 1), null, new BigDecimal("2000000")),
                LocalDate.of(2025, 6, 30), MotivoTerminacion.RENUNCIA);

        // Con los parametros de 2025 el salario de 2.000.000 tiene auxilio de 200.000.
        assertEquals(new BigDecimal("2200000"), anterior.baseCesantias());
    }

    @Test
    @DisplayName("El contrato a termino fijo exige fecha de vencimiento pactada")
    void contratoFijoExigeVencimiento() {
        assertThrows(IllegalArgumentException.class,
                () -> new Contrato(TipoContrato.FIJO, INGRESO, null, SALARIO));
    }

    @Test
    @DisplayName("La fecha de retiro no puede ser anterior al ingreso")
    void retiroAnteriorAlIngreso() {
        Contrato contrato = new Contrato(TipoContrato.INDEFINIDO, RETIRO, null, SALARIO);
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.liquidar(contrato, INGRESO, MotivoTerminacion.RENUNCIA));
    }
}
