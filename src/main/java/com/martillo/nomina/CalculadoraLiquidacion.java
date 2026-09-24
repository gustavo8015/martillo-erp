package com.martillo.nomina;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Motor de calculo de la liquidacion de un contrato de trabajo (HU-07).
 *
 * Reglas implementadas:
 *  - Cesantias: (salario + auxilio) x dias del anio / 360 (CST, art. 249).
 *  - Intereses sobre cesantias: cesantias x dias x 12 % / 360 (Ley 52 de 1975).
 *  - Prima de servicios: (salario + auxilio) x dias del semestre / 360 (CST, art. 306).
 *  - Vacaciones: salario sin auxilio x dias trabajados / 720 (CST, art. 186).
 *  - Indemnizacion por despido sin justa causa segun el tipo (CST, art. 64).
 *
 * Los dias se cuentan con el calendario comercial de 30 dias por mes y 360 por
 * anio, tratando el ultimo dia de cada mes como dia 30.
 *
 * Supuestos declarados en el informe del Sprint 3: la prima del semestre
 * anterior ya fue pagada, el trabajador no ha disfrutado vacaciones desde su
 * ingreso, el salario del ultimo periodo esta pagado y el salario es ordinario.
 */
@Service
public class CalculadoraLiquidacion {

    private static final BigDecimal D360 = new BigDecimal("360");
    private static final BigDecimal D720 = new BigDecimal("720");
    private static final BigDecimal TASA_INTERESES = new BigDecimal("0.12");
    private static final BigDecimal DIAS_MES = new BigDecimal("30");
    private static final BigDecimal DIEZ = BigDecimal.valueOf(10);
    private static final BigDecimal DOS = BigDecimal.valueOf(2);

    private final ParametrosLegalesRepository parametros;

    public CalculadoraLiquidacion(ParametrosLegalesRepository parametros) {
        this.parametros = parametros;
    }

    public Liquidacion liquidar(Contrato contrato, LocalDate retiro, MotivoTerminacion motivo) {
        if (retiro == null) throw new IllegalArgumentException("La fecha de retiro es obligatoria");
        if (retiro.isBefore(contrato.ingreso())) {
            throw new IllegalArgumentException("La fecha de retiro no puede ser anterior al ingreso");
        }

        ParametrosLegales p = parametros.vigentesEn(retiro);
        BigDecimal salario = contrato.salarioMensual();

        boolean conAuxilio = salario.compareTo(p.smmlv().multiply(DOS)) <= 0;
        BigDecimal baseCesantias = conAuxilio ? salario.add(p.auxilioTransporte()) : salario;

        long diasTotal = dias360(contrato.ingreso(), retiro);
        long diasAnio = dias360(maximo(contrato.ingreso(), LocalDate.of(retiro.getYear(), 1, 1)), retiro);
        long diasSemestre = dias360(maximo(contrato.ingreso(), inicioSemestre(retiro)), retiro);

        BigDecimal cesantias = pesos(baseCesantias.multiply(BigDecimal.valueOf(diasAnio))
                .divide(D360, 6, RoundingMode.HALF_UP));
        BigDecimal intereses = pesos(cesantias.multiply(BigDecimal.valueOf(diasAnio))
                .multiply(TASA_INTERESES).divide(D360, 6, RoundingMode.HALF_UP));
        BigDecimal prima = pesos(baseCesantias.multiply(BigDecimal.valueOf(diasSemestre))
                .divide(D360, 6, RoundingMode.HALF_UP));
        BigDecimal vacaciones = pesos(salario.multiply(BigDecimal.valueOf(diasTotal))
                .divide(D720, 6, RoundingMode.HALF_UP));

        BigDecimal indemnizacion = motivo == MotivoTerminacion.SIN_JUSTA_CAUSA
                ? indemnizacion(contrato, retiro, diasTotal, p)
                : BigDecimal.ZERO;

        BigDecimal total = cesantias.add(intereses).add(prima).add(vacaciones).add(indemnizacion);

        return new Liquidacion(diasTotal, diasAnio, diasSemestre, baseCesantias,
                cesantias, intereses, prima, vacaciones, indemnizacion, total);
    }

    /** Indemnizacion por terminacion unilateral del empleador sin justa causa (CST, art. 64). */
    private BigDecimal indemnizacion(Contrato contrato, LocalDate retiro, long diasTotal, ParametrosLegales p) {
        BigDecimal dias = switch (contrato.tipo()) {
            case FIJO -> BigDecimal.valueOf(Math.max(0, dias360(retiro.plusDays(1), contrato.finPactado())));
            case OBRA_LABOR -> BigDecimal.valueOf(Math.max(15, dias360(retiro.plusDays(1), contrato.finPactado())));
            case INDEFINIDO -> diasIndemnizacionIndefinido(contrato, diasTotal, p);
        };

        // Una sola division al final, para no acumular el error de redondear el valor del dia.
        return pesos(contrato.salarioMensual().multiply(dias).divide(DIAS_MES, 2, RoundingMode.HALF_UP));
    }

    /**
     * Dias de indemnizacion de un contrato a termino indefinido: 30 dias por el
     * primer anio (20 si el salario es de 10 salarios minimos o mas) mas 20 o 15
     * dias por cada anio adicional, proporcional a la fraccion.
     */
    private BigDecimal diasIndemnizacionIndefinido(Contrato contrato, long diasTotal, ParametrosLegales p) {
        boolean salarioAlto = contrato.salarioMensual().compareTo(p.smmlv().multiply(DIEZ)) >= 0;
        BigDecimal primerAnio = salarioAlto ? BigDecimal.valueOf(20) : BigDecimal.valueOf(30);
        BigDecimal porAnioAdicional = salarioAlto ? BigDecimal.valueOf(15) : BigDecimal.valueOf(20);

        if (diasTotal <= 360) return primerAnio;

        BigDecimal fraccion = BigDecimal.valueOf(diasTotal - 360).divide(D360, 10, RoundingMode.HALF_UP);
        return primerAnio.add(porAnioAdicional.multiply(fraccion));
    }

    /**
     * Dias entre dos fechas con el calendario comercial de 360 dias, contando
     * ambos extremos. El ultimo dia de cada mes cuenta como dia 30.
     */
    static long dias360(LocalDate a, LocalDate b) {
        if (b.isBefore(a)) return 0;
        return (b.getYear() - a.getYear()) * 360L
                + (b.getMonthValue() - a.getMonthValue()) * 30L
                + (dia(b) - dia(a)) + 1;
    }

    private static int dia(LocalDate f) {
        return f.getDayOfMonth() == f.lengthOfMonth() ? 30 : f.getDayOfMonth();
    }

    private static LocalDate inicioSemestre(LocalDate f) {
        return f.getMonthValue() <= 6
                ? LocalDate.of(f.getYear(), 1, 1)
                : LocalDate.of(f.getYear(), 7, 1);
    }

    private static LocalDate maximo(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private static BigDecimal pesos(BigDecimal valor) {
        return valor.setScale(0, RoundingMode.HALF_UP);
    }
}
