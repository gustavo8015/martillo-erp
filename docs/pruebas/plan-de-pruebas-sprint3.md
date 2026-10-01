# Plan y resultados de pruebas del Sprint 3 (Guía 8)

Modelo de calidad: ISO/IEC 25010:2023. Ejecución: GitHub Actions, flujo
`construccion.yml`, corrida [36904479555](https://github.com/gustavo8015/martillo-erp/actions/runs/36904479555)
sobre el commit `e2ca984`, 1 de octubre de 2026.

## Requisitos de calidad

| Historia | Característica ISO/IEC 25010 | Requisito medible |
|----------|------------------------------|-------------------|
| HU-07 | Adecuación funcional: corrección | Total de 6.372.159 (indefinido), 11.372.159 (fijo) y 7.622.159 (obra o labor) |
| HU-08 | Adecuación funcional: completitud y corrección | Alerta única al cruzar el umbral; la reposición la cierra |
| HU-09 | Seguridad: confidencialidad y responsabilidad | 403 a todo rol sin permiso y registro en la pista de auditoría |
| HU-10 | Capacidad de interacción: inclusividad | Contraste de 7:1 o más (WCAG 2.2, 1.4.6) y preferencia conservada |
| HU-11 | Seguridad: autenticidad y resistencia | Puntuación menor a 0,5 rechazada; sin clave secreta falla cerrado |
| Transversal | Eficiencia de desempeño | 10.000 liquidaciones en menos de 5 s |
| Transversal | Fiabilidad: disponibilidad | `/actuator/health` responde UP |
| Transversal | Mantenibilidad: capacidad de ser probado | Cobertura de líneas del backend de 80 % o más |

## Niveles y resultados

| Nivel | Herramienta | Ubicación | Casos | Aprobados |
|-------|-------------|-----------|------:|----------:|
| Unitario y rendimiento | JUnit 5, Mockito | `src/test/java/com/martillo/{nomina,inventario,seguridad,catalog,consignment}` | 33 | 33 |
| Integración | Spring Boot Test, MockMvc, H2 | `src/test/java/com/martillo/integracion` | 16 | 16 |
| Interfaz | Playwright 1.56, Chromium | `frontend/pruebas/e2e` | 13 | 13 |
| **Total** | | | **62** | **62** |

Cobertura JaCoCo del backend: 89,9 % de líneas (525 de 584) y 77,1 % de ramas (84 de 109).

## Defectos encontrados

- **D-01.** La interfaz mostraba solo "Error de validacion" cuando el servidor
  enviaba el detalle por campo en el objeto `errores`. Corregido en
  `frontend/js/api.js`; regresión cubierta por CP-E2E-13 y CP-07.4.
- **D-02.** `InventarioServiceTest.salidaMayorAlStock` fallaba con
  `UnnecessaryStubbingException` en la primera ejecución real de Maven.
  Corregido con un stub `lenient` y verificación de que no hubo escritura.

## Cómo ejecutar

```bash
# Backend: pruebas unitarias, de integracion y cobertura (target/site/jacoco)
mvn clean verify

# Interfaz: desde frontend/pruebas
npm install
npx playwright install chromium
npx playwright test          # reporte en resultados/reporte-html
```

Las capturas de `evidencias/` las tomó Playwright durante la ejecución; las de
`evidencias/jira/` muestran el registro de esta evidencia en las historias KAN-57 a KAN-61.
El informe completo está en `docs/Guia8-Testing-Sprint3-MartilloERP.docx`.
