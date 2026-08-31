# Mapa Urbano

Plataforma cívica para registrar, ubicar y dar seguimiento a problemas de infraestructura urbana.

El repositorio reúne la documentación técnica, el diseño del producto y los directorios reservados para cada aplicación. Las implementaciones se incorporarán mediante ramas de tarea y pull requests.

## Arquitectura objetivo

- Aplicación Android nativa: Kotlin + Jetpack Compose.
- Panel web administrativo: Angular + TypeScript + Leaflet.
- Mapas del panel: Leaflet.
- Backend compartido: Kotlin + Ktor, con API REST y WebSocket.
- Persistencia: PostgreSQL + PostGIS.
- Acceso ciudadano: cuenta opcional con `Mis reportes` o envío anónimo mediante código de seguimiento.
- Evidencia fotográfica: binarios `BYTEA` y metadatos en PostgreSQL.
- Operación: Docker, HTTPS, backups y base de datos administrada.
- Sin Node.js en producción, Express, SQLite, React ni servidor web separado para el panel. Node.js/npm se usan únicamente para desarrollar y compilar Angular.

## Estructura prevista

```text
Mapa Urbano/
├── .github/         # Plantillas, reglas de revisión y checks del repositorio
├── backend-ktor/     # Monolito modular y servidor de archivos del panel
├── android-app/      # Aplicación Android nativa
├── admin-web/        # Aplicación Angular del panel administrativo
├── database/         # Migraciones, seeds y documentación del esquema
├── docs/             # Arquitectura, contratos, UX, seguridad y roadmap
└── scripts/          # Verificaciones locales y de integración continua
```

## Colaboración

- [Guía para contribuir](CONTRIBUTING.md)
- [Responsabilidades del equipo](docs/10-equipo-y-responsabilidades.md)
- [Estrategia de ramas y puntos de integración](docs/11-flujo-git-y-ramas.md)
- [Política de seguridad](SECURITY.md)

## Documentación

1. [Estado, alcance y trazabilidad](docs/00-estado-y-alcance.md)
2. [Arquitectura de solución](docs/01-arquitectura.md)
3. [Módulos del backend](docs/02-modulos-backend.md)
4. [Modelo de datos y PostGIS](docs/03-modelo-de-datos.md)
5. [API REST y tiempo real](docs/04-api-y-tiempo-real.md)
6. [UX e interfaces](docs/05-ux-e-interfaces.md)
7. [Roadmap de implementación](docs/06-roadmap.md)
8. [Operación y seguridad](docs/07-operacion-y-seguridad.md)
9. [Decisiones de arquitectura](docs/08-decisiones.md)
10. [Checklist de inicio](docs/09-checklist-de-inicio.md)
11. [Equipo y responsabilidades](docs/10-equipo-y-responsabilidades.md)
12. [Flujo Git y estrategia de ramas](docs/11-flujo-git-y-ramas.md)
13. [Entidades de base de datos y DDL de ejemplo](docs/12-entidades-bd-ejemplo.md)

La fuente de verdad son los archivos Markdown dentro de `docs/`. Los PDF de trabajo y exportaciones locales no se versionan.

## Estado actual

| Área | Estado |
|---|---|
| Revisión del documento original | Completa |
| Arquitectura objetivo | Documentada |
| Contrato inicial de datos y API | Documentado, pendiente de aprobación |
| Mockups de interfaces | Incluidos |
| Diseño de interfaz | Documentado mediante mockups |
| Fundación ejecutable del backend y rutas base | Completa |
| Lógica funcional de panel, backend y Android | Pendiente |
| Despliegue | Pendiente |
