# Proceso actual y próximos desarrollos

## Propósito y fecha de corte

Este documento permite revisar qué existe, cómo trabaja el equipo y cuál es el próximo incremento demostrable.

Fecha de corte: **14 de septiembre de 2026**.

La evidencia más reciente integrada se encuentra en `develop`. `main` conserva la última línea estable, pero todavía no contiene los PR #6 y #7. Un cambio abierto o un archivo local no se considera integrado.

## Estado actual verificable

| Componente | Estado | Qué puede demostrarse | Evidencia | Pendiente |
|---|---|---|---|---|
| Alcance, arquitectura y contratos | Documentado; requiere aprobación de las decisiones abiertas | Lectura de requisitos, arquitectura, datos, API, UX y seguridad | [`docs/`](README.md) y [PR #2](https://github.com/Jquincal/Mapa-Urbano/pull/2) | Aprobar contratos, proveedor de base, SDK de mapas y políticas de datos |
| Fundación Ktor | Implementada como base técnica | El proyecto compila; las rutas están registradas; liveness responde `200`, readiness `503` y las rutas de negocio responden `501` | [PR #2](https://github.com/Jquincal/Mapa-Urbano/pull/2), [`backend-ktor/README.md`](../backend-ktor/README.md) y pruebas de rutas | Repositorios, autenticación, permisos, validaciones y casos de uso reales |
| Base PostgreSQL/PostGIS | Implementada y probada en un entorno descartable | Flyway crea doce tablas y seis categorías; las pruebas cubren integridad, repetición, rollback y restauración de datos sintéticos | [PR #6](https://github.com/Jquincal/Mapa-Urbano/pull/6), [CI 34286113649](https://github.com/Jquincal/Mapa-Urbano/actions/runs/34286113649) y [`database/README.md`](../database/README.md) | Conectar la base con los endpoints y probar restauración en staging con datos representativos |
| Diseño ciudadano | Prototipo y especificación; no es una aplicación implementada | Se pueden recorrer pantallas y consultar capturas, limitaciones y casos de prueba | [PR #7](https://github.com/Jquincal/Mapa-Urbano/pull/7) e [informe de Stitch](13-verificacion-stitch.md) | Resolver los defectos del informe y construir la aplicación Android en Compose |
| Verificación adicional de Stitch | En revisión; no integrada | El [PR #9](https://github.com/Jquincal/Mapa-Urbano/pull/9) agrega capturas y pruebas adicionales | PR #9 abierto | Revisión humana, CI e integración en `develop` |
| Aplicación Android | Pendiente | Solo existen alcance, navegación objetivo y criterios de aceptación | [`android-app/README.md`](../android-app/README.md) | Proyecto Compose y flujo conectado al backend |
| Panel administrativo Angular | Pendiente | Solo existen alcance y criterios de aceptación | [`admin-web/README.md`](../admin-web/README.md) | Proyecto Angular, autenticación y gestión real de reportes |
| Despliegue | Pendiente | No existe un entorno desplegado que demuestre el flujo completo | [Operación y seguridad](07-operacion-y-seguridad.md) | Imagen reproducible, staging, HTTPS, observabilidad y recuperación probada |

### Regla para declarar un avance

Un componente se marca **implementado** solo cuando el código está integrado y existe una prueba o demostración reproducible. Una especificación, un mockup, un prototipo, una ruta `501` o un PR abierto se identifican con su estado real.

## Proceso actual de trabajo

El equipo usa `develop` como rama de integración y `main` como línea estable. Las tareas se realizan en ramas cortas y se integran mediante pull requests.

| Etapa | Forma de trabajo | Evidencia obligatoria |
|---|---|---|
| Planificación | Crear un issue `MU-<número>` antes de programar. Debe indicar objetivo, responsables, dependencias, entregable y criterio de finalización. | Enlace al issue desde el PR |
| Inicio | Crear una rama desde `develop` con el formato `<tipo>/MU-<número>-<descripción>`. | Rama asociada a una sola tarea |
| Desarrollo | Realizar cambios pequeños y mantener contratos, migraciones y documentación sincronizados. | Commits claros y pruebas proporcionales al riesgo |
| Revisión | Abrir un PR hacia `develop` y completar todos los campos de la plantilla. Nadie aprueba su propio cambio. | Revisor del área, conversaciones resueltas y CI verde |
| Integración | Usar *Squash and merge* para tareas. Cerrar el issue y eliminar la rama ya integrada. | Commit en `develop`, issue cerrado y rama retirada |
| Entrega estable | Crear `release/vX.Y.Z` desde `develop`; integrar la release en `main` y devolver las correcciones a `develop`. | PR de release, etiqueta y demostración de la versión |

### Situación observada y correcciones inmediatas

Al 14 de septiembre no hay issues del proyecto que permitan seguir las tareas `MU-*`. Además, varios PR anteriores conservaron campos vacíos de la plantilla. El PR #8 intentó integrar `develop` directamente en `main`, quedó cerrado sin fusión y no siguió el flujo de release documentado.

Antes de iniciar nuevas funciones se debe:

1. Crear los issues de los procesos prioritarios y asignar responsables.
2. Completar objetivo, dependencias, evidencia y criterio de salida en cada PR nuevo.
3. Resolver el PR #9 mediante revisión e integración o cierre justificado.
4. Retirar las ramas remotas de tareas ya fusionadas.
5. Publicar esta actualización en la rama visible para la revisión del proyecto mediante el flujo de release.

## Recorte de prioridad

### Primer incremento demostrable

El primer objetivo es demostrar el ciclo central con la menor cantidad de dependencias:

```text
Vecino anónimo crea un reporte
              ↓
El backend lo guarda y devuelve un código opaco
              ↓
Un administrador consulta el reporte y cambia su estado
              ↓
El vecino consulta el nuevo estado con el código
```

Este incremento incluye ubicación, categoría, título y descripción. La fotografía es opcional y no bloquea la primera demostración. El administrador debe autenticarse antes de modificar un reporte.

### Trabajo posterior dentro del MVP

- Cuentas de vecinos, sesiones y `Mis reportes`.
- Fotografía con validación y almacenamiento transaccional.
- Mapa, filtros y detalle completos en Android y Angular.
- Asignación, prioridad, fecha objetivo, historial y auditoría administrativa.
- Estadísticas y actualización por WebSocket.
- Docker, staging, HTTPS, backups y observabilidad.

### Funciones fuera del MVP

Se mantienen fuera del MVP: iOS, login social, recuperación de contraseña, notificaciones push, chat, asignación automática, inteligencia artificial, planificación avanzada de cuadrillas, analítica compleja y multi-tenancy.

## Próximos procesos

Los identificadores `P-*` ordenan este plan. Antes de comenzar cada proceso se debe crear su issue `MU-*` y reemplazar el identificador provisional por el enlace real.

| Orden | Proceso y objetivo | Responsables | Dependencias | Entregable verificable | Finaliza cuando |
|---|---|---|---|---|---|
| P-01 | Corregir trazabilidad y publicar el estado actual | Joaquín; revisión de cada responsable de área | `develop`, PR #6, PR #7 y decisión sobre PR #9 | Este documento enlazado desde los índices; issues prioritarios creados; PR documental completo | La documentación está revisada, CI está verde y el estado actualizado es accesible desde la rama publicada |
| P-02 | Implementar alta anónima y consulta por código | Juan y Aldana; revisión de Joaquín | Esquema V1/V2 integrado y contrato de reportes aprobado | Repositorio y endpoints reales para categorías, alta y seguimiento; pruebas con PostgreSQL/PostGIS | El alta devuelve un código opaco, la consulta recupera el mismo reporte y esos endpoints dejan de responder `501` |
| P-03 | Implementar operación administrativa mínima | Juan y Santi; apoyo de Aldana y QA de Quimey | P-02, autenticación administrativa y reglas de transición aprobadas | Login administrativo, listado, detalle y cambio de estado con auditoría | Un administrador autenticado cambia el estado y una prueba integrada verifica persistencia, permisos e historial |
| P-04 | Construir el flujo ciudadano mínimo en Android | Joaquín y Mauro | P-02 y SDK de mapas aprobado | Aplicación Compose con mapa, formulario anónimo, confirmación y seguimiento por código | El recorrido funciona contra el backend real en un dispositivo y conserva el estado ante errores previstos |
| P-05 | Construir el panel Angular mínimo | Santi y Quimey; integración con Juan | P-03 y contrato administrativo estable | Aplicación Angular con login, lista, detalle y cambio de estado | El flujo funciona con teclado, a 375 px y contra la API real, sin datos simulados |
| P-06 | Preparar demostración integrada | Mauro y Joaquín; participación de todo el equipo | P-02 a P-05 | Entorno reproducible, datos sintéticos, instrucciones, capturas y enlaces a CI/PR | Otra persona puede ejecutar el recorrido completo y obtener el resultado esperado sin ayuda del autor |

## Evidencia e instrucciones de demostración

### Documentación

Desde la raíz del repositorio:

```bash
python3 scripts/check_docs.py
```

Resultado esperado: `Documentation validation passed` sin enlaces locales rotos.

### Fundación del backend

Desde `backend-ktor/`:

```bash
./gradlew test
```

Resultado esperado: compilación correcta y pruebas de rutas aprobadas. Las respuestas `501` de negocio son el límite esperado de la fundación actual, no una función terminada.

### Migraciones y base de datos

Con JDK 21 y Docker disponibles, desde `backend-ktor/`:

```bash
./gradlew check
```

Resultado esperado: pruebas unitarias e integración con PostgreSQL/PostGIS aprobadas. El procedimiento de migración y las variables necesarias están en [`database/README.md`](../database/README.md).

### Diseño ciudadano

Revisar el [informe de Stitch](13-verificacion-stitch.md), sus capturas y su matriz. Los recorridos parciales muestran decisiones de UX; no demuestran Android, backend ni persistencia real.

## Mantenimiento de este documento

Joaquín actualiza la fecha y el resumen después de cada integración relevante. El responsable de cada área confirma su estado y evidencia durante la revisión del PR. Los enlaces rotos, estados sin evidencia y responsables genéricos bloquean la publicación documental.
