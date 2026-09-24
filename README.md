# Martillo ERP

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

## Sprint 3

Entregas funcionales del Sprint 3, con su documentación de arquitectura.

| Historia | Entrega | Dónde está |
|----------|---------|------------|
| **HU-07** | Motor de liquidación de contratos según su tipo | `src/main/java/com/martillo/nomina/` |
| **HU-08** | Alerta de stock crítico del inventario | `src/main/java/com/martillo/inventario/` |
| **HU-09** | Control de acceso por roles verificado en el servidor | `src/main/java/com/martillo/seguridad/` |
| **HU-10** | Modo de alto contraste, WCAG 2.2 nivel AAA | `frontend/css/estilos.css`, `frontend/js/sprint3.js` |
| **HU-11** | Verificación reCAPTCHA v3 en el inicio de sesión | `src/main/java/com/martillo/seguridad/` |
| **TT-01** | ADR, diagramas C4 y despliegue | `docs/adr/`, `docs/diagramas/`, `infra/k8s/` |

### Motor de liquidación

Calcula cesantías, intereses sobre cesantías, prima de servicios, vacaciones e
indemnización con el calendario comercial de 30 días por mes y 360 por año.
Base normativa: Decreto 2663 de 1950 (Código Sustantivo del Trabajo), artículos
64, 186, 249 y 306, y Ley 52 de 1975.

Caso de referencia verificado en `CalculadoraLiquidacionTest`: salario de
$2.500.000, ingreso el 1 de enero de 2026 y retiro el 30 de septiembre de 2026
sin justa causa.

| Concepto | Indefinido | Término fijo | Obra o labor |
|----------|-----------:|-------------:|-------------:|
| Cesantías | 2.061.821 | 2.061.821 | 2.061.821 |
| Intereses sobre cesantías | 185.564 | 185.564 | 185.564 |
| Prima de servicios | 687.274 | 687.274 | 687.274 |
| Vacaciones | 937.500 | 937.500 | 937.500 |
| Indemnización sin justa causa | 2.500.000 | 7.500.000 | 3.750.000 |
| **Total a pagar** | **6.372.159** | **11.372.159** | **7.622.159** |

El salario mínimo y el auxilio de transporte no son constantes del código sino
parámetros con fecha de vigencia (ADR-005), de modo que la liquidación de un
periodo cerrado se reproduce.

### Usuarios de la demostración

| Usuario | Rol | Alcance |
|---------|-----|---------|
| `admin` | Administrador | Administración, catálogo e inventario |
| `nomina` | Responsable de nómina | Solo nómina y liquidación |
| `catalogo` | Catalogador | Catálogo e inventario |
| `auditor` | Auditor | Lectura de catálogo e inventario, y auditoría |

Contraseña de demostración: `martillo2026`. Este directorio es únicamente para
el Sprint Review y se sustituye por el directorio institucional a través de la
interfaz `DirectorioUsuarios`.

### Variables de entorno

| Variable | Para qué sirve |
|----------|----------------|
| `MARTILLO_RECAPTCHA_SECRETO` | Clave secreta de reCAPTCHA v3. Sin ella la verificación falla cerrada |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Conexión a PostgreSQL |
| `DB_PRINCIPAL_PASSWORD`, `DB_NOMINA_PASSWORD` | Contraseñas de las dos bases en Docker Compose |

Ninguna credencial se escribe en el repositorio y el flujo de integración
continua lo verifica en cada Pull Request.

### Infraestructura

```bash
# Ambiente actual (ADR-002)
DB_PRINCIPAL_PASSWORD=... DB_NOMINA_PASSWORD=... \
  docker compose -f infra/docker-compose.yml up --build

# Destino documentado, no aplicado todavía
cat infra/k8s/README.md
```

### Documentación

- Decisiones de arquitectura: [`docs/adr/`](docs/adr/)
- Diagramas C4 y de despliegue: [`docs/diagramas/`](docs/diagramas/)
- Documentación arc42 completa: Notion (ver el informe del sprint)

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

