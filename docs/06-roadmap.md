# Roadmap de implementación

## Objetivo del plan

Construir un MVP operable con Android, panel web Angular y backend compartido, manteniendo la arquitectura modular. Node.js/npm se utilizarán para el desarrollo y build del frontend, pero no como runtime de producción.

Las horas son estimaciones iniciales. Cada tarea está dimensionada para una entrega de 2 a 8 horas y debe recalibrarse después de cerrar las decisiones abiertas.

## Hitos

| Hito | Resultado | Criterio de salida |
|---|---|---|
| H0 — Documentación aprobada | Contratos, modelo, UX y operación alineados. | Se cierran las preguntas de `00-estado-y-alcance.md`. |
| H1 — Fundación ejecutable | Ktor, Docker, configuración y health checks. | Backend arranca localmente y valida dependencias. |
| H2 — Reportes persistentes | PostgreSQL/PostGIS, categorías y CRUD público. | Se puede crear y consultar un reporte con ubicación. |
| H3 — Evidencia y seguimiento | Imágenes `BYTEA`, endpoint binario y código anónimo. | Un vecino recibe su código y la evidencia persiste con el reporte. |
| H4 — Panel administrativo | Login, tabla, mapa, filtros, estado, prioridad y delegación. | Un admin gestiona y asigna un reporte end-to-end. |
| H5 — Android | Mapa, alta, fotografía y seguimiento. | Flujo principal probado en un dispositivo real. |
| H6 — Tiempo real | WebSocket para reportes y estadísticas. | Cambios visibles sin recargar ambas interfaces. |
| H7 — Operación | HTTPS, backups, observabilidad y pruebas de recuperación. | Despliegue de staging repetible y restauración validada. |

## Fase 0 — Documentación y decisiones

Estado: en progreso en esta entrega.

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Aprobar alcance y sustituciones tecnológicas | 2 h | Producto/equipo | — | Se aprueba la tabla de trazabilidad. |
| Cerrar proveedor y capacidad de PostgreSQL/PostGIS | 2 h | Operación/DB | — | Existen entorno, límites, backup y proyección de imágenes. |
| Elegir SDK de mapas para Android | 2 h | Android/UX | — | La decisión incluye tiles, licencia y offline. |
| Revisar mockups y flujos con stakeholders | 4 h | UX/producto | — | Se anotan cambios y aceptación. |

## Fase 1 — Fundación del repositorio y Ktor

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Crear repositorio con raíz de proyecto clara | 2 h | Equipo | H0 | El repositorio no incluye el home del usuario. |
| Definir módulos y convenciones Kotlin | 4 h | Backend | H0 | Los límites de paquetes coinciden con la arquitectura. |
| Configurar Ktor, serialización, logging y errores | 6 h | Backend | H0 | El proceso devuelve errores consistentes. |
| Crear imagen Docker reproducible | 4 h | Backend/DevOps | H0 | La imagen final arranca sin Node como runtime y sin secretos embebidos; el build multietapa compila Angular. |
| Agregar `/health/live` y `/health/ready` | 3 h | Backend | Ktor | Se distinguen proceso vivo y dependencias listas. |
| Documentar variables de entorno | 2 h | Backend/DevOps | Docker | Existe una lista de configuración sin valores secretos. |

## Fase 2 — PostgreSQL/PostGIS y reportes

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Crear migración base y extensión PostGIS | 4 h | Backend/DB | H1 | La base crea el esquema en un entorno limpio. |
| Insertar categorías iniciales idempotentes | 2 h | Backend/DB | Migración base | Repetir la migración no duplica categorías. |
| Implementar repositorio geoespacial | 6 h | Backend/DB | PostGIS | Búsqueda por `bbox` usa índice espacial. |
| Implementar alta pública con validación | 6 h | Backend | Esquema | Devuelve `201`, estado inicial y código. |
| Implementar detalle y listado paginado | 6 h | Backend | Alta | Los límites evitan descargas ilimitadas. |
| Implementar historial de estado | 4 h | Backend | Reportes | Cada transición queda asociada a un actor. |

## Fase 3 — Media y seguimiento anónimo

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Crear migración `report_images` con `BYTEA` | 4 h | Backend/DB | Fase 2 | Binario y metadatos se crean con restricciones verificables. |
| Validar MIME, firma, tamaño y checksum | 4 h | Backend | H1 | Los archivos inválidos se rechazan antes de persistir. |
| Integrar imagen con creación de reporte | 6 h | Backend | Fase 2 + migración | Reporte e imagen se confirman o revierten en la misma transacción. |
| Implementar endpoint binario y caché | 4 h | Backend | Imagen persistente | Responde MIME, tamaño, ETag, `nosniff` y autorización correctos. |
| Implementar consulta por `trackingCode` | 4 h | Backend | Fase 2 | Devuelve solo información pública y tiene rate limit. |
| Medir backup/restore con imágenes | 4 h | DB/DevOps | Datos representativos | Se conocen tamaño, RPO/RTO y tiempo de restauración. |

## Fase 4 — Panel web administrativo Angular

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Crear aplicación Angular y configuración de build | 6 h | Web/UX | H0 | El proyecto usa TypeScript, componentes standalone, configuración de entornos y build reproducible. |
| Crear shell, routing y servicios de API | 6 h | Web | API pública | Las rutas, servicios tipados e interceptores de sesión funcionan con el mismo origen. |
| Integrar Leaflet y capas de reportes | 6 h | Web | Listado | El mapa respeta filtros, límites y ciclo de vida de Angular. |
| Implementar login y sesión | 4 h | Web/Backend | Auth | La sesión expira y el error es genérico. |
| Implementar tabla, filtros y detalle | 8 h | Web | Admin API | Tabla, mapa y detalle representan el mismo estado. |
| Implementar cambio de estado y eliminación | 6 h | Web/Backend | Auth + reportes | Hay confirmación, feedback y auditoría. |
| Implementar equipos y asignación | 8 h | Web/Backend/DB | Auth + reportes | Un reporte se asigna o reasigna con historial y control de concurrencia. |
| Implementar prioridad y fecha objetivo | 6 h | Web/Backend/DB | Auth + reportes | La cola se filtra y ordena por urgencia y vencimiento. |
| Compilar Angular y servirlo desde Ktor | 4 h | Backend/Web | Ktor + build Angular | `/admin` y sus rutas internas funcionan desde la imagen final sin Node runtime. |

## Fase 5 — Android Kotlin + Jetpack Compose

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Crear navegación y tema Compose | 6 h | Android | H0 | Las pantallas base respetan el sistema visual. |
| Implementar mapa y consulta REST | 8 h | Android | SDK mapa + API | Carga marcadores dentro de la vista. |
| Implementar formulario y validación | 8 h | Android | API de alta | Los errores no borran los datos del formulario. |
| Implementar cámara/galería y previsualización | 6 h | Android | Media | El usuario ve y puede reemplazar la foto. |
| Implementar confirmación y copia de código | 4 h | Android | Alta | El código se muestra completo una sola vez. |
| Implementar seguimiento por código | 6 h | Android | Endpoint público | El vecino ve estado e historial público. |
| Probar tamaños, permisos y estados offline | 6 h | Android/QA | Flujos | No hay scroll horizontal ni dead ends. |

## Fase 6 — Tiempo real

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Implementar bus de eventos interno | 4 h | Backend | Reportes | Eventos se emiten después del commit. |
| Exponer WebSocket público y administrativo | 6 h | Backend | Auth + eventos | Cada canal entrega datos apropiados. |
| Manejar reconexión en panel | 3 h | Web | WS | Reconsulta REST al reconectar. |
| Manejar reconexión en Android | 4 h | Android | WS | El estado queda consistente tras volver online. |
| Probar concurrencia y eventos duplicados | 4 h | QA/Backend | WS | Clientes toleran reordenamiento o duplicación definida. |

## Fase 7 — Calidad y operación

| Tarea | Esfuerzo | Responsable sugerido | Dependencia | Terminado cuando |
|---|---:|---|---|---|
| Pruebas de contrato API | 6 h | QA/Backend | API estable | Errores, estados y paginación están cubiertos. |
| Pruebas de carga del mapa y CRUD | 6 h | QA/DevOps | Fases 2–6 | Se mide RNF-01 y RNF-04 con datos definidos. |
| Revisión de seguridad | 6 h | Backend/QA | Flujo completo | Upload, auth, rate limit y CSRF revisados. |
| Configurar HTTPS y secretos | 4 h | DevOps | Staging | No hay tráfico ni secretos inseguros. |
| Configurar backups y restauración | 6 h | DevOps/DB | Base administrada | Se restaura en entorno de prueba. |
| Manual de operación y rollback | 4 h | DevOps | Despliegue | Existe procedimiento paso a paso y responsable. |

## Dependencias críticas

```text
Decisiones aprobadas
        ↓
Ktor + Docker ──→ API y migraciones ──→ Panel web
        │                  │              ↑
        │                  └──→ Android ──┘
        └──→ PostgreSQL/media BYTEA ──→ Seguimiento y evidencia

Reportes + commit confirmado ──→ Eventos ──→ WebSocket
```

## Riesgos y mitigaciones

| Riesgo | Impacto | Probabilidad | Mitigación |
|---|---|---|---|
| Reglas de delegación o prioridad no están acordadas | Alto | Media | Cerrar equipos, permisos, valores y conflictos antes del sprint administrativo. |
| Servicio de tiles limita o cambia condiciones | Alto | Media | Elegir proveedor con límites documentados y plan alternativo. |
| Código de seguimiento perdido | Alto | Media | Mostrarlo una sola vez con copiar/compartir y explicar la consecuencia. |
| Crecimiento de imágenes degrada la base o backups | Alto | Media | Límite de 5 MB, consultas sin binario, métricas de volumen y pruebas periódicas de restore. |
| Muchas conexiones WebSocket | Medio | Baja | Límites, heartbeat, escalado horizontal y re-sincronización REST. |
| Datos sensibles en logs o auditoría | Alto | Baja | Lista explícita de campos prohibidos y revisión automatizada. |
| Confusión entre repositorio del proyecto y home | Alto | Media | Crear/confirmar una raíz Git dentro de `Mapa Urbano` antes de programar. |

## Reserva de planificación

Agregar 20–30% de buffer sobre las estimaciones después de validar proveedor de mapas, capacidad de PostgreSQL, autenticación y política de datos.
