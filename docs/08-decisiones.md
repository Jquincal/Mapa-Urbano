# Decisiones de arquitectura

Este registro evita volver a discutir decisiones ya tomadas. Las decisiones abiertas se mantienen en [00-estado-y-alcance.md](00-estado-y-alcance.md).

## ADR-001 — Monolito modular

**Estado:** Aprobada.

**Decisión:** Usar un único backend desplegable con módulos internos y límites explícitos.

**Motivo:** El MVP necesita velocidad de entrega, una sola transacción de negocio y operación simple. Los módulos permiten evolucionar sin introducir microservicios prematuramente.

**Consecuencia:** Se debe cuidar el acoplamiento entre paquetes y mantener contratos internos claros. El escalado horizontal se puede agregar sin separar los módulos.

## ADR-002 — Kotlin + Ktor y Angular

**Estado:** Aprobada.

**Decisión:** El backend será Kotlin + Ktor. El panel será Angular + TypeScript, compilado a archivos estáticos y servido por Ktor.

**Motivo:** El mismo ecosistema Kotlin cubre backend y Android. Angular aporta estructura, routing, tipado y componentes reutilizables para el panel administrativo. Node.js/npm quedan limitados al desarrollo y la compilación; no se requieren en producción.

**Consecuencia:** `admin-web` tendrá `package.json`, dependencias de Angular y un build reproducible. Docker usará una etapa de build con Node.js y una imagen final de Ktor sin Node runtime. Se deben mantener compatibilidad de navegadores, assets versionados y fallback para rutas del panel.

## ADR-003 — PostgreSQL + PostGIS

**Estado:** Aprobada.

**Decisión:** Sustituir SQLite por PostgreSQL administrado con extensión PostGIS.

**Motivo:** La aplicación depende de filtros por área, proximidad y límites geográficos. PostgreSQL también ofrece transacciones y concurrencia adecuadas para el panel.

**Consecuencia:** Las migraciones deben activar PostGIS y las pruebas deben cubrir índices espaciales y restauración.

## ADR-004 — Android nativo más panel administrativo web

**Estado:** Aprobada.

**Decisión:** Separar la experiencia ciudadana en Android de la experiencia operativa municipal en web.

**Motivo:** El flujo de reporte necesita cámara, geolocalización y controles táctiles; el municipio necesita tabla, filtros y densidad de información de escritorio.

**Consecuencia:** Ambas superficies deben compartir contratos de API y eventos, pero pueden tener modelos de presentación distintos. iOS queda fuera del MVP.

## ADR-005 — Fotografías en S3-compatible

**Estado:** Reemplazada por ADR-010.

**Decisión:** Guardar archivos en un bucket privado compatible con S3 y solo metadatos en PostgreSQL.

**Motivo:** Evita inflar backups de la base y permite políticas de ciclo de vida independientes.

**Consecuencia:** El backend necesita manejar fallos entre la subida y la transacción, además de URLs firmadas y limpieza de objetos huérfanos.

Esta decisión se conserva como registro histórico y no debe implementarse en el MVP actual.

## ADR-006 — Código de seguimiento como bearer token

**Estado:** Aprobada para MVP.

**Decisión:** Cada reporte anónimo recibe un código aleatorio, no secuencial y sin cuenta de vecino.

**Motivo:** Mantiene baja la fricción de alta y permite consultar el estado.

**Consecuencia:** La pérdida del código no tiene recuperación en el MVP. El valor debe mostrarse y copiarse con claridad, y el acceso debe protegerse con rate limit y respuestas genéricas.

## ADR-007 — REST para sincronización inicial, WebSocket para cambios

**Estado:** Aprobada.

**Decisión:** REST carga y consulta el estado; WebSocket comunica actualizaciones después del commit.

**Motivo:** REST es simple de reintentar y depurar. WebSocket evita recargas para eventos que cambian mapa, tabla y estadísticas.

**Consecuencia:** Todo cliente necesita estrategia de reconexión y relectura por REST. WebSocket no transporta fotografías.

## ADR-008 — Leaflet en el panel

**Estado:** Aprobada.

**Decisión:** Usar Leaflet para el mapa web y servir sus assets de forma controlada desde el panel.

**Motivo:** Es suficiente para marcadores, capas y filtros del MVP y mantiene la interfaz web sin framework obligatorio.

**Consecuencia:** Hay que confirmar proveedor de tiles, atribución, límites de uso y plan de caché antes del staging.

## ADR-009 — Delegación y prioridad manual en el MVP

**Estado:** Aprobada.

**Decisión:** El panel permitirá asignar un reporte a un equipo municipal, a un responsable individual o a ambos. También permitirá definir prioridad `low`, `medium`, `high` o `urgent` y una fecha objetivo opcional.

**Motivo:** La vista administrativa necesita convertir reportes visibles en una cola de trabajo operativa. Sin responsable ni orden explícito, el municipio puede observar problemas pero no coordinar su atención.

**Consecuencia:** El backend incorpora un módulo `assignments`, historial de asignaciones y prioridad, control de concurrencia y permisos específicos. El MVP no incluye asignación automática, optimización de recorridos ni balanceo inteligente de carga.

## ADR-010 — Imágenes almacenadas en PostgreSQL

**Estado:** Aprobada.

**Decisión:** Guardar el binario de cada fotografía en `report_images.data BYTEA`, junto con MIME, tamaño, dimensiones, checksum y fecha. El MVP admite como máximo una imagen de 5 MB por reporte.

**Motivo:** El proyecto requiere que la base de datos contenga las imágenes. Para el alcance inicial, una única transacción y un único mecanismo de backup simplifican consistencia y recuperación.

**Consecuencia:** Los listados nunca seleccionan la columna binaria, las imágenes se entregan desde endpoints dedicados y los backups incluyen evidencia fotográfica. Se deben medir crecimiento, I/O y restauración. Si el volumen futuro supera los límites operativos, una nueva ADR podrá introducir almacenamiento de objetos detrás del mismo módulo `media`.
