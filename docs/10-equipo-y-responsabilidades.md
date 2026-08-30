# Equipo y responsabilidades

## Propósito

Este documento define qué debe conocer cada integrante, qué entregables conduce y quién acompaña su trabajo. La responsabilidad principal implica asegurar definición, coordinación, calidad y cierre; no significa trabajar en soledad.

## Conocimientos comunes

Todo el equipo debe comprender, como mínimo:

- Objetivo del producto, usuarios, alcance del MVP y flujos ciudadano/administrativo.
- Arquitectura Android + Angular + Ktor + PostgreSQL/PostGIS, incluidas imágenes `BYTEA`.
- Flujo básico de Git, pull requests, revisión y resolución de conflictos simples.
- Fundamentos de HTTP, JSON, códigos de estado y contrato de API.
- Uso local de Docker, variables de entorno y separación de secretos.
- Definition of Done, criterios de aceptación y comunicación temprana de bloqueos.
- Reglas mínimas de seguridad: validación, autorización, privacidad y datos prohibidos en logs.

## Joaquín - jefe de proyecto, arquitectura y Android

### Debe dominar

- Planificación ágil, dependencias, capacidad y riesgos.
- Arquitectura modular y contratos entre clientes, backend y datos.
- Android con Kotlin, Compose, Navigation, ViewModel, StateFlow y coroutines.
- Permisos, cámara, networking, mapas, estados offline y WebSocket.
- Estrategia de releases, observabilidad y criterios de seguridad.

### Responsabilidades

- Mantener alcance, backlog, prioridades, objetivos de sprint y registro de decisiones.
- Cerrar contratos junto con Juan y Aldana antes de que dependan de ellos las interfaces.
- Liderar Android: mapa, alta, foto, confirmación y seguimiento.
- Revisar cambios de arquitectura e integración y resolver bloqueos de alcance.
- Coordinar demos, staging y decisión de salida de cada versión.

### Entregables

- Backlog priorizado con criterios de aceptación.
- Contrato de integración aprobado.
- Aplicación Android y sus pruebas principales.
- Registro de decisiones técnicas.
- Checklist de release y plan de rollback.

Apoyo principal: Mauro en Android y operación; Juan en backend; Aldana en datos; Santi en contratos consumidos por Angular.

## Juan - responsable de backend Ktor

### Debe dominar

- Kotlin desde Java: null safety, data classes, sealed classes y coroutines.
- Ktor: routing, plugins, serialización, sesiones, StatusPages, multipart y WebSocket.
- Capas `api`, `application`, `domain` e `infrastructure`.
- Transacciones, paginación, errores consistentes, idempotencia y control de concurrencia.
- Auth, CSRF, rate limit, autorización, uploads y pruebas de integración.

### Responsabilidades

- Crear la base ejecutable de Ktor, configuración, logging, errores y health checks.
- Implementar auth, reportes, categorías, asignaciones, prioridad, media, estadísticas, notificaciones y auditoría.
- Publicar eventos solo después del commit y sin datos sensibles.
- Mantener ejemplos de request/response y acompañar la integración de ambos clientes.
- Cubrir reglas y endpoints críticos con pruebas automatizadas.

### Entregables

- Fundación Ktor modular.
- API pública y administrativa.
- Autenticación, permisos y auditoría.
- Persistencia/entrega de imágenes y WebSocket.
- Suite de pruebas backend.

Apoyo principal: Aldana en persistencia, Joaquín en arquitectura y seguridad, Santi/Mauro en pruebas desde clientes.

## Aldana - responsable de datos y persistencia

### Debe dominar

- PostgreSQL: tipos, restricciones, índices, transacciones y `EXPLAIN`.
- Almacenamiento `BYTEA`, TOAST, impacto en I/O, capacidad y restauración.
- PostGIS: `geography(Point, 4326)`, GIST, bounding box y distancias.
- Migraciones Flyway, seeds idempotentes y compatibilidad entre versiones.
- Historial, auditoría, borrado lógico, retención, backup y restauración.
- Fundamentos de repositorios Kotlin para revisar persistencia con Juan.

### Responsabilidades

- Diseñar y mantener el esquema relacional/geoespacial.
- Crear migraciones para reportes, usuarios, equipos, asignaciones, prioridad, adjuntos e historial.
- Definir índices y medir las consultas de mapa, filtros y estadísticas.
- Preparar datos de prueba realistas y verificaciones de integridad.
- Ejecutar y documentar una restauración completa en staging.

### Entregables

- Modelo y diccionario de datos.
- Migraciones reproducibles desde una base vacía.
- Consultas geoespaciales medidas.
- Integridad de asignación/prioridad.
- Procedimiento probado de backup y restore.

Apoyo principal: Juan en repositorios, Mauro en servicios locales y restauración, Joaquín en decisiones de contrato.

## Santi - responsable del panel Angular

### Debe dominar

- TypeScript estricto y Angular con componentes standalone.
- Inyección, Router, guards, formularios reactivos, HttpClient e interceptores.
- Signals y RxJS para estado, cancelación y sincronización.
- Leaflet, clustering, límites de mapa y limpieza del ciclo de vida.
- Accesibilidad WCAG AA, diseño responsive y pruebas de componentes.

### Responsabilidades

- Crear el proyecto Angular y organizarlo por funcionalidades a partir de los contratos aprobados.
- Implementar shell, sesión, rutas, servicios tipados y manejo de errores.
- Construir dashboard, reportes, detalle, delegación, prioridades, métricas y equipos.
- Integrar API real y WebSocket con resincronización REST.
- Garantizar teclado, foco, zoom 200%, responsive y estados de carga/vacío/error.

### Entregables

- Fundación Angular reproducible.
- Gestión de reportes con tabla y mapa sincronizados.
- Delegación y prioridad operativa.
- Categorías, estadísticas, auditoría y configuración.
- Pruebas e integración desde assets servidos por Ktor.

Apoyo principal: Quimey en UI/QA, Juan en API, Joaquín en alcance e integración.

## Quimey - soporte frontend, accesibilidad y QA visual

### Debe dominar

- Git básico, HTML semántico, CSS responsive y TypeScript inicial.
- Componentes Angular, templates, inputs/outputs y formularios.
- Tokens de diseño, variantes y estados de interacción.
- Teclado, foco, contraste, etiquetas y mensajes accesibles.
- Pruebas exploratorias y reportes de defectos reproducibles.

### Responsabilidades

- Construir componentes reutilizables junto con Santi.
- Implementar vistas acotadas sobre servicios y contratos ya definidos.
- Verificar escritorio, tablet, móvil y zoom 200%.
- Ejecutar QA visual, accesibilidad y regresión del panel.
- Registrar incidencias con pasos, evidencia, severidad y resultado esperado.

### Entregables

- Biblioteca de componentes UI.
- Vistas frontend asignadas.
- Matriz responsive.
- Checklist de accesibilidad.
- Reporte de QA web.

Apoyo principal: Santi mediante pairing y revisión; Joaquín para priorizar defectos.

## Mauro - soporte Android, integración y DevOps

### Debe dominar

- Kotlin/Compose básicos, navegación, permisos y consumo de ViewModels.
- HTTP, multipart, herramientas de prueba de API y WebSocket.
- Docker Compose, redes, volúmenes, health checks e imágenes multietapa.
- CI, logs, artefactos, staging, HTTPS, secretos y rollback.
- Pruebas móviles con red lenta, reconexión, cámara y dispositivos reales.

### Responsabilidades

- Apoyar historias Android acotadas y pruebas manuales.
- Mantener el entorno local reproducible.
- Crear CI para backend, Angular y Android.
- Preparar smoke tests y staging.
- Documentar arranque, diagnóstico, despliegue, backup y rollback.

### Entregables

- Docker Compose y configuración de ejemplo.
- Pipeline de integración continua.
- Historias Android asignadas.
- Staging reproducible.
- Runbook operativo.

Apoyo principal: Joaquín en Android, Juan en Ktor, Aldana en PostgreSQL/restore.

## Matriz de propiedad

| Área | Responsable | Apoyo/revisión |
|---|---|---|
| Dirección, alcance y decisiones | Joaquín | Todo el equipo |
| Contratos REST/WebSocket | Juan + Joaquín | Santi, Aldana |
| Backend Ktor | Juan | Joaquín, Aldana |
| PostgreSQL/PostGIS | Aldana | Juan, Mauro |
| Panel Angular | Santi | Quimey, Joaquín |
| Sistema visual y QA web | Quimey | Santi |
| Android | Joaquín | Mauro |
| Docker, CI y staging | Mauro | Juan, Aldana |
| Seguridad técnica | Juan + Joaquín | Mauro, Aldana |
| Pruebas integradas | Mauro + Quimey | Responsable de cada módulo |
| Release y demo | Joaquín | Todo el equipo |

## Definition of Done común

Una tarea termina cuando:

- Cumple criterios de aceptación y no agrega alcance oculto.
- Respeta arquitectura, convenciones y seguridad.
- Incluye pruebas proporcionales al riesgo.
- Contempla carga, vacío, validación, error y permisos cuando corresponda.
- Actualiza contratos, migraciones y documentación afectados.
- Supera CI, revisión por otra persona y prueba integrada relevante.
- Puede demostrarse y verificarse con instrucciones claras.
