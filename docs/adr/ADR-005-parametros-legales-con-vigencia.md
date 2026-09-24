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
- Los valores de 2026 quedaron contrastados con el texto oficial: Decreto 1469
  del 29 de diciembre de 2025, que fija el salario minimo en $1.750.905, y
  Decreto 1470 de la misma fecha, que fija el auxilio de transporte en
  $249.095. Ambos rigen desde el 1 de enero de 2026. El impedimento normativo
  declarado en el informe del Sprint 3 queda cerrado.
- El Decreto 1469 estuvo suspendido provisionalmente por el Consejo de Estado
  entre el 13 de febrero y julio de 2026, cuando la suspension fue revocada y
  el decreto volvio a regir. El episodio confirma la decision: si los valores
  fueran constantes del codigo, cada cambio judicial obligaria a recompilar y
  las liquidaciones ya practicadas dejarian de reproducirse.
