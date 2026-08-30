# Contribuir a Mapa Urbano

## Antes de comenzar

1. Lee el [alcance](docs/00-estado-y-alcance.md), la [arquitectura](docs/01-arquitectura.md) y el [flujo Git](docs/11-flujo-git-y-ramas.md).
2. Confirma que la tarea tiene identificador `MU-<número>`, responsable y criterios de aceptación.
3. Verifica dependencias con contratos, migraciones u otras tareas.
4. Nunca subas secretos, configuraciones locales, builds, dependencias instaladas ni PDF de trabajo.

## Flujo básico

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/MU-123-area-descripcion
```

Realiza commits pequeños y verificables. Antes de abrir el pull request:

```bash
git fetch origin
git merge origin/develop
python3 scripts/check_docs.py
git diff --check
```

## Commits

Formato recomendado:

```text
<tipo>(<área>): descripción breve
```

Tipos frecuentes: `feat`, `fix`, `docs`, `test`, `refactor`, `chore`.

Ejemplos:

```text
feat(backend): agrega alta anónima de reportes
feat(web): incorpora filtro por prioridad
fix(database): corrige índice de asignación activa
docs(architecture): documenta control de concurrencia
```

## Pull requests

- Destino normal: `develop`.
- Una tarea o cambio coherente por pull request.
- Incluye cómo probar, capturas para UI y riesgos conocidos.
- Actualiza documentación cuando cambia comportamiento, contrato o despliegue.
- Solicita revisión según el área definida en [equipo y responsabilidades](docs/10-equipo-y-responsabilidades.md).
- No apruebes tu propio cambio.
- Usa `Squash and merge` para ramas de tarea.

## Criterio de terminado

El cambio cumple la Definition of Done del equipo, CI está en verde, la revisión está resuelta y el flujo afectado puede demostrarse.
