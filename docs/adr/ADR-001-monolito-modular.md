# ADR-001. Monolito modular para el dominio de la subasta y servicio de nomina separado

- **Estado:** aceptada
- **Fecha:** 24 de septiembre de 2026
- **Sprint:** decidida en el Sprint 2, reafirmada en el Sprint 3

## Contexto

El equipo es de tres personas con dedicacion parcial y el dominio de la casa
de subastas todavia se esta descubriendo sprint a sprint. Al mismo tiempo, la
nomina maneja datos personales y salariales cuyo tratamiento exige medidas
reforzadas segun la Ley 1581 de 2012, solo el rol de responsable de nomina
debe alcanzarla, y sus reglas cambian por norma legal con un ritmo distinto al
del resto del sistema.

## Decision

El dominio de la subasta se construye como un monolito modular en Java con
Spring Boot: un unico artefacto con modulos de consignantes, catalogo y lotes,
subastas, inventario, y seguridad y roles, sobre una sola base de datos. La
nomina se despliega como una unidad separada, con su propia base de datos y
sus propias credenciales.

El resultado es una arquitectura hibrida, un monolito modular mas un servicio
satelite, y no una arquitectura de microservicios: hay dos unidades
desplegables, no una malla de servicios pequenos.

## Consecuencias

- Se conservan las transacciones locales dentro del monolito. El movimiento de
  inventario y su alerta de stock critico se confirman juntos o no se confirma
  ninguno, sin coordinar dos servicios (HU-08).
- Los datos salariales quedan aislados en su propia base, alcanzable solo por
  el servicio de nomina.
- A cambio, hay dos unidades que desplegar y una llamada de red cuando la
  aplicacion web integra la nomina.
- La arquitectura hexagonal se aplica dentro de los modulos y no compite con
  esta decision: el motor de liquidacion consulta sus parametros legales por
  una interfaz y no desde constantes del codigo.
- En el Sprint 3 ambos servicios comparten artefacto y se separan por perfil y
  por puerto; el paso siguiente es separar tambien el artefacto cuando el
  modulo de nomina lo justifique.
