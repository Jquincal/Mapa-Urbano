# Documentación de Mapa Urbano

Esta carpeta es la fuente de verdad de la arquitectura objetivo, el alcance y el plan de construcción. Se versionan Markdown e imágenes; los PDF de trabajo y exportaciones se mantienen fuera de Git.

## Orden recomendado de lectura

1. [Estado, alcance y trazabilidad](00-estado-y-alcance.md)
2. [Arquitectura](01-arquitectura.md)
3. [Modelo de datos](03-modelo-de-datos.md)
4. [API y tiempo real](04-api-y-tiempo-real.md)
5. [UX e interfaces](05-ux-e-interfaces.md)
6. [Roadmap](06-roadmap.md)
7. [Operación y seguridad](07-operacion-y-seguridad.md)
8. [Decisiones](08-decisiones.md)
9. [Checklist de inicio](09-checklist-de-inicio.md)
10. [Equipo y responsabilidades](10-equipo-y-responsabilidades.md)
11. [Flujo Git y estrategia de ramas](11-flujo-git-y-ramas.md)
12. [Entidades de base de datos y DDL de ejemplo](12-entidades-bd-ejemplo.md)
13. [Migraciones ejecutables, configuración y pruebas](../database/README.md)
14. [Verificación de la interfaz en Google Stitch](13-verificacion-stitch.md)

## Regla de alcance

La documentación describe la arquitectura objetivo; no implica que todos sus flujos estén implementados.
Al 9 de septiembre de 2026 existen la fundación Ktor y las migraciones Flyway y pruebas reales
de PostgreSQL/PostGIS, integradas en `develop` mediante el [PR #6](https://github.com/Jquincal/Mapa-Urbano/pull/6).
El estado detallado y las limitaciones están en [estado y alcance](00-estado-y-alcance.md).
Los flujos productivos se incorporan mediante las ramas y pull requests definidos en este directorio.

## Revisión ciudadana de Stitch — septiembre 2026

La documentación incorpora el inventario visual y la navegación del MVP. Consultar [resultados, capturas y limitaciones del prototipo](13-verificacion-stitch.md) antes de implementar. Las pantallas generadas no acreditan por sí solas conexiones funcionales ni pruebas Android.
