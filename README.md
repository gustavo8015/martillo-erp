# Martillo ERP — Backend (Sprint 1)

Servicio backend para la gestión básica de **Piezas** y **Consignantes**, construido con
**Java 17 + Spring Boot 3 + Spring Data JPA**, siguiendo principios SOLID (SRP y DIP
explícitamente aplicados vía interfaces de servicio e inyección de dependencias).

## Sprint Goal

> Al final del Sprint 1, tendremos un servicio funcional para la gestión básica de
> Piezas y Consignantes, incluyendo su creación y listado, con la arquitectura inicial
> documentada en arc42 y siguiendo los principios SOLID.

## Historias de usuario implementadas

- **HU-01** — Registrar una pieza en el catálogo.
- **HU-02** — Registrar un consignante con su información de contacto.
- **HU-03** — Ver la lista de todos los consignantes registrados.

## Cómo correrlo (perfil por defecto: H2 en memoria, sin instalar nada más)

```bash
mvn spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.
Consola H2 (para ver los datos guardados): `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:martillodb`, usuario `sa`, sin contraseña).

## Cómo correrlo con PostgreSQL (perfil de producción)

Ajusta las credenciales en `application-postgres.properties` y ejecuta:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Endpoints disponibles

| Método | Endpoint | Historia de Usuario |
|---|---|---|
| POST | `/api/piezas` | HU-01 |
| GET | `/api/piezas` | Listado de piezas |
| GET | `/api/piezas/{id}` | Detalle de pieza |
| POST | `/api/consignantes` | HU-02 |
| GET | `/api/consignantes` | HU-03 |
| GET | `/api/consignantes/{id}` | Detalle de consignante |

### Ejemplo — registrar un consignante (para el Sprint Review / evidencia)

```bash
curl -X POST http://localhost:8080/api/consignantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCompleto": "Laura Gómez",
    "tipoDocumento": "CC",
    "numeroDocumento": "123456789",
    "telefono": "3001234567",
    "email": "laura@example.com",
    "direccion": "Calle 10 # 20-30, Bogotá"
  }'
```

### Ejemplo — registrar una pieza

```bash
curl -X POST http://localhost:8080/api/piezas \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Jarrón de porcelana",
    "descripcion": "Jarrón chino del siglo XIX",
    "categoria": "Antigüedades",
    "precioReserva": 1500000
  }'
```

## Pruebas unitarias

```bash
mvn test
```

Incluye pruebas para `ConsignanteServiceImpl` y `PiezaServiceImpl` (casos de éxito y de
error), cumpliendo el punto del DoD sobre pruebas automatizadas.

## Estructura de paquetes (coincide con el diagrama de paquetes del proyecto)

```
com.martillo
├── catalog        (Pieza: entidad, repositorio, servicio, controlador, DTOs)
├── consignment     (Consignante: entidad, repositorio, servicio, controlador, DTOs)
└── common          (excepciones y manejo de errores compartido)
```

## Nota de verificación

Este código fue escrito siguiendo convenciones estándar de Spring Boot 3.x /
Jakarta EE, pero **no pudo compilarse dentro de este entorno** por no tener acceso a
Maven Central. Verifícalo con `mvn clean install` en tu máquina antes de dar por
cerrado el DoD ("las pruebas unitarias pasan").

