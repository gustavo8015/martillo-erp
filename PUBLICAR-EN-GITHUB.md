# Cómo publicar este repositorio en GitHub

El repositorio local ya está listo: tiene los commits del Sprint 1, el archivo `.gitignore`
y las ramas `main` y `develop` creadas. Solo falta enlazarlo con GitHub, que exige la cuenta
del equipo y por eso debe hacerlo un integrante.

## Paso 1. Crear el repositorio vacío en GitHub

Entrar a https://github.com/new y crear un repositorio **privado** llamado `martillo-erp`.
No marcar las opciones de README, .gitignore ni licencia: el repositorio local ya los trae y
marcarlas obliga a resolver un conflicto en el primer envío.

## Paso 2. Enlazar y enviar

Desde esta carpeta, en la terminal:

```bash
git remote add origin https://github.com/<usuario>/martillo-erp.git
git push -u origin main
git push -u origin develop
```

Reemplazar `<usuario>` por el nombre de la cuenta de GitHub.

## Paso 3. Proteger la rama develop

En GitHub, entrar a Settings, Branches, Add branch protection rule, escribir `develop` como
patrón y activar "Require a pull request before merging" con una aprobación requerida. Esto
implementa la regla acordada en la Definition of Done: ninguna rama de funcionalidad entra a
`develop` sin la revisión de otro integrante.

## Paso 4. Invitar al equipo

Settings, Collaborators, Add people, e invitar a los demás integrantes.

## Flujo de trabajo por historia

```bash
git checkout develop
git pull
git checkout -b feature/HU-04-nombre-corto
# ... trabajo, pruebas y documentación ...
git add -A
git commit -m "HU-04: descripción breve del cambio"
git push -u origin feature/HU-04-nombre-corto
```

Luego abrir el Pull Request hacia `develop` desde la interfaz de GitHub.

## Paso 5. Publicar el frontend con GitHub Pages

Este paso da una direccion web propia para la interfaz, sin depender de ningun servicio externo.
Requisito: el repositorio debe ser publico, porque GitHub Pages sobre repositorios privados exige
plan de pago.

1. En GitHub, entrar a Settings, General, y en Danger Zone usar "Change visibility" para dejar el
   repositorio publico.
2. Entrar a Settings, Pages. En "Source" elegir "Deploy from a branch". En "Branch" elegir `main`
   y la carpeta `/ (root)`. Guardar.
3. Esperar entre uno y dos minutos. La direccion queda asi:

```
https://<usuario>.github.io/martillo-erp/
```

El archivo `index.html` de la raiz redirige automaticamente a `frontend/index.html`, de modo que
esa direccion abre la interfaz completa.

Importante: la interfaz publicada entra en modo demostracion con datos de ejemplo, porque el
backend corre en localhost y no es accesible desde internet. Para la demostracion con datos
reales hay que levantar el backend y servir la carpeta `frontend` en el equipo, como explica
`frontend/README.md`.
