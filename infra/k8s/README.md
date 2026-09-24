# Manifiestos de Kubernetes

Destino de despliegue documentado en el Sprint 3. **No se aplica todavia**: el
ambiente actual usa Docker Compose en un solo host (ADR-002).

Criterio de adopcion acordado por el equipo; basta con que se cumpla una de las
tres condiciones:

1. Se necesita mas de un servidor.
2. Se necesitan actualizaciones sin interrupcion durante una subasta en vivo.
3. Se necesita escalar de forma independiente el servicio de inteligencia artificial.

## Orden de aplicacion

```bash
kubectl apply -f 00-namespace.yaml

# Las credenciales se crean en el cluster, nunca en el repositorio.
kubectl -n martillo-erp create secret generic db-principal --from-literal=password="$DB_PRINCIPAL_PASSWORD"
kubectl -n martillo-erp create secret generic db-nomina    --from-literal=password="$DB_NOMINA_PASSWORD"
kubectl -n martillo-erp create secret generic recaptcha    --from-literal=secreto="$RECAPTCHA_SECRETO"

kubectl apply -f 30-statefulset-bases.yaml
kubectl apply -f 20-deployment-principal.yaml
kubectl apply -f 21-deployment-nomina.yaml
kubectl apply -f 40-networkpolicy-nomina.yaml
kubectl apply -f 50-ingress.yaml
```

## Equivalencia con Docker Compose

| Docker Compose (hoy)                 | Kubernetes (destino)                  |
|--------------------------------------|----------------------------------------|
| Servicio del archivo compose         | Deployment y Service                   |
| Volumen de la base de datos          | StatefulSet con PersistentVolumeClaim  |
| Variable de entorno con la contrasena| Secret                                 |
| Verificacion de salud (healthcheck)  | Sondas readiness y liveness            |
| Red de Compose                       | NetworkPolicy                          |
| Replicas manuales                    | replicas y autoescalado                |
