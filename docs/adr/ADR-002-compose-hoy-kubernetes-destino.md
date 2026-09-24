# ADR-002. Docker Compose como despliegue actual; Kubernetes como destino

- **Estado:** Compose aceptada; Kubernetes propuesta
- **Fecha:** 24 de septiembre de 2026
- **Sprint:** 3

## Contexto

Los servicios ya se empaquetan como imagenes Docker y se orquestan con Docker
Compose en un solo host. Kubernetes anade una capa de operacion completa
(cluster, redes, almacenamiento, observabilidad) que un equipo de tres
personas con dedicacion parcial no necesita mientras un host baste. El equipo
tampoco cuenta hoy con un cluster donde contrastar los manifiestos.

## Decision

El ambiente actual sigue siendo Docker Compose. Los manifiestos de Kubernetes
se escriben y se versionan en `infra/k8s` como destino documentado, pero no se
aplican en el Sprint 3.

Kubernetes se adopta cuando se cumpla al menos una de estas tres condiciones:

1. Se necesita mas de un servidor.
2. Se necesitan actualizaciones sin interrupcion durante una subasta en vivo.
3. Se necesita escalar de forma independiente el servicio de inteligencia artificial.

## Consecuencias

- Se evita pagar el costo operativo de Kubernetes antes de necesitar su beneficio.
- La migracion es predecible, porque casi cada elemento del archivo de Compose
  tiene un equivalente directo: Deployment y Service, StatefulSet con
  PersistentVolumeClaim, Secret, sondas readiness y liveness, y NetworkPolicy.
- El diagrama de despliegue no se puede contrastar con un despliegue real y se
  valida unicamente contra el archivo de Compose. Queda como impedimento
  declarado del Sprint 3.
- El criterio de adopcion se revisa en cada retrospectiva.
