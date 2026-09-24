# ADR-004. Servicio de asistencia de inteligencia artificial opcional y diferido

- **Estado:** propuesta
- **Fecha:** 24 de septiembre de 2026
- **Sprint:** 3

## Contexto

El catalogo de una casa de subastas se beneficiaria de dos capacidades
apoyadas en modelos Transformer: busqueda semantica de lotes, que encuentra
piezas por su descripcion y no solo por palabras exactas, y borradores de
descripcion de lotes que el catalogador revisa antes de publicar.

## Decision

El servicio de asistencia se define como un contenedor propio detras de una
interfaz del dominio, aparece con borde punteado en el diagrama C4 de nivel 2
y no se implementa en el Sprint 3.

## Consecuencias

- El sistema no adquiere dependencia de un proveedor de modelos ni asume sus
  costos ni su latencia antes de tener evidencia de valor.
- Al quedar detras de una interfaz, el proveedor se puede sustituir sin tocar
  el resto del sistema.
- Cualquier texto que se envie a un tercero exige revisar el tratamiento de
  datos personales; los borradores generados siempre los aprueba una persona.
- La decision se revisa cuando el catalogo alcance un volumen en el que la
  busqueda por palabras exactas deje de ser suficiente.
