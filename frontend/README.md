# Martillo ERP — Frontend (Sprint 1)

Interfaz web para las tres historias de usuario del Sprint 1. No requiere instalación,
compilación ni dependencias: es HTML, CSS y JavaScript estándar.

## Historias cubiertas

- **HU-01** Registrar una pieza en el catálogo. Formulario con validación en el navegador
  equivalente a la del servidor (nombre, categoría y precio de reserva obligatorios).
- **HU-02** Registrar un consignante con su información de contacto.
- **HU-03** Ver el listado de consignantes registrados, con búsqueda por nombre o documento.

## Cómo ejecutarlo

1. Levantar el backend en otra terminal:

```bash
mvn spring-boot:run
```

2. Servir el frontend desde esta carpeta:

```bash
cd frontend
python -m http.server 5173
```

3. Abrir `http://localhost:5173` en el navegador.

El backend ya permite CORS desde cualquier puerto de `localhost` (`CorsConfig.java`), de modo
que no hace falta configuración adicional.

## Comportamiento sin backend

Si la API no responde, la interfaz entra en **modo demostración** con datos de ejemplo y lo
indica en la esquina superior derecha. Esto permite mostrar el diseño en la Sprint Review
aunque el servicio no esté levantado.

## Estructura

```
frontend/
├── index.html          Estructura de las dos pantallas
├── css/estilos.css     Estilos y paleta
└── js/
    ├── api.js          Acceso a la API REST (única capa que conoce las URL)
    └── app.js          Validación, render de listados y eventos
```

La separación entre `api.js` y `app.js` aplica el Principio de Responsabilidad Única: si
cambia la dirección del servicio o el formato de transporte, solo se modifica `api.js`.

## Configurar otra dirección de API

Antes de cargar `js/api.js`, definir la variable global:

```html
<script>window.MARTILLO_API_URL = 'http://192.168.1.10:8080';</script>
```
