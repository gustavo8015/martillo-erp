# ADR-005. Parametros legales con vigencia por fuera del codigo

- **Estado:** aceptada
- **Fecha:** 24 de septiembre de 2026
- **Sprint:** decidida en el Sprint 2, aplicada al motor de liquidacion en el Sprint 3

## Contexto

El salario minimo, el auxilio de transporte y los porcentajes de deduccion
cambian cada anio por norma. Si estos valores viven como constantes del
codigo, la liquidacion de un periodo cerrado deja de reproducirse en cuanto
cambia la vigencia, y cualquier recalculo posterior daria un resultado
distinto al que se pago.

## Decision

Los parametros legales se modelan como registros con fecha de vigencia. El
motor de liquidacion los consulta a traves de la interfaz
`ParametrosLegalesRepository` segun la fecha de retiro del contrato, y nunca
desde constantes del codigo.

## Consecuencias

- La liquidacion de un periodo cerrado se reproduce con los valores vigentes
  en su fecha, lo que es verificable con una prueba automatica.
- El motor no conoce el origen de los parametros: hoy es una implementacion en
  memoria y manana puede ser una tabla o un servicio, sin tocar el calculo.
- Agregar una vigencia nueva es un cambio de datos y no de logica.
- Los valores de 2026 provienen de una fuente secundaria y deben contrastarse
  con el decreto oficial antes de llevar el motor a produccion. Impedimento
  declarado en el informe del Sprint 3.
