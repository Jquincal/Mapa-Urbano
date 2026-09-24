# Estado, alcance y trazabilidad

## Propósito

Este documento adapta el documento original **Mapa Colaborativo de Problemas Urbanos v2.0** a la arquitectura solicitada para Mapa Urbano.

Fecha de actualización: 9 de septiembre de 2026.

La carpeta de trabajo se llama `Mapa Urbano`, aunque la ruta solicitada decía `Mapa Urban`. Se trabajó sobre la carpeta existente.

## Estado de implementación y evidencia

MU-205 está implementada en el [PR #6](https://github.com/Jquincal/Mapa-Urbano/pull/6),
ya integrado en `develop`. Este estado corresponde a esa entrega, no a un despliegue productivo.

| Entrega | Estado verificable |
|---|---|
| Fundación Ktor | Módulos y rutas registrados; liveness responde `200`, readiness `503` y negocio `501`. |
| V1 — Esquema | Doce tablas, PostGIS, enums, restricciones de autoría e imagen única, índices y triggers de `updated_at`. |
| V2 — Categorías | Seis categorías documentadas; la carga idempotente conserva personalizaciones. |
| Ejecución de migraciones | `./gradlew migrateDatabase`, configurado por entorno; clean y baseline automático deshabilitados. |
| Verificación | Pruebas de rutas y once pruebas de base con Testcontainers, incluyendo rollback y backup/restauración. |
| API persistente | Pendiente: conexión de repositorios, autenticación, validaciones y casos de uso. |
| Operación | Pendiente: staging, cuenta de aplicación, backups administrados, mediciones de capacidad y RPO/RTO. |

La [ejecución de CI 34286113649](https://github.com/Jquincal/Mapa-Urbano/actions/runs/34286113649)
verificó el código de MU-205 en `3d873ec`: backend y documentación aprobados. Los informes
están disponibles como `backend-test-reports`. Las pruebas usan datos sintéticos y una
base descartable; no acreditan una restauración de staging ni el flujo completo de fotografías.

El [procedimiento de base de datos](../database/README.md) detalla configuración, pruebas
y adopción de bases preexistentes. El siguiente paso técnico es conectar la persistencia
al backend e implementar autenticación y alta de reportes respetando los contratos.

## Objetivo del producto

Permitir que un vecino registre un problema urbano desde Android, con ubicación y evidencia opcional, y que el municipio lo gestione desde un panel web administrativo.

El vecino puede crear una cuenta para conservar sus reportes y consultar su evolución, o realizar un reporte anónimo y seguirlo mediante un código opaco. El municipio obtiene una vista geográfica, operativa y auditable sin exponer datos personales en las vistas públicas.

## Qué se conserva del documento original

- Mapa con reportes geolocalizados.
- Alta de reportes desde una ubicación seleccionada.
- Categoría, título, descripción y fotografía opcional.
- Estados `Pendiente`, `En proceso` y `Resuelto`.
- Panel administrativo con filtros, tabla y estadísticas básicas.
- Acceso restringido para administradores.
- Validación de imágenes y diseño usable en pantallas pequeñas.
- Actualización del mapa sin recargar la interfaz.

## Sustituciones aprobadas

| Documento original | Arquitectura objetivo | Motivo |
|---|---|---|
| React + Leaflet | Angular + TypeScript + Leaflet | El panel tendrá una estructura mantenible, routing, tipado y componentes reutilizables. Node.js/npm se utilizarán solo durante desarrollo y build. |
| Node.js + Express | Kotlin + Ktor | Un backend único comparte lenguaje, tipos y despliegue con la aplicación Android. |
| SQLite | PostgreSQL + PostGIS | Se necesita integridad transaccional, concurrencia y consultas geoespaciales. |
| Una aplicación web responsive | Android nativo + panel web administrativo | Se separa la experiencia ciudadana de la operación municipal. |
| Fotos en una ruta local | PostgreSQL `BYTEA` en `report_images` | Mantiene reporte, imagen y metadatos bajo una única transacción y política de backup para el MVP. |
| iOS como posibilidad futura | Fuera del MVP | No forma parte del alcance actual. |
| Cuenta obligatoria de vecino | Cuenta opcional o reporte anónimo con `tracking_code` | Permite conservar reportes en una cuenta sin impedir el acceso anónimo. |

## Alcance del MVP

### Incluido

- Aplicación Android para ver el mapa, registrarse, iniciar sesión, crear reportes y consultar su seguimiento.
- Cuenta opcional de vecino con correo, nombre visible y contraseña protegida mediante hash adaptativo.
- Listado privado de los reportes creados con la cuenta autenticada.
- Reportes anónimos disponibles sin registro, incluso como elección explícita de un vecino autenticado.
- Código de seguimiento opaco generado únicamente para reportes anónimos y mostrado después de crearlos.
- Categorías administrables con un conjunto inicial predefinido.
- Ubicación como punto geográfico en WGS84.
- Fotografía opcional almacenada en PostgreSQL, con validación de tipo, tamaño, firma y dimensiones.
- Panel web para iniciar sesión, consultar reportes, filtrar, ver el mapa, cambiar estados y eliminar reportes con confirmación.
- Delegación manual de reportes a un equipo municipal o a un administrador responsable.
- Prioridad operativa manual (`low`, `medium`, `high`, `urgent`) y fecha objetivo opcional.
- Historial de estados y auditoría de acciones administrativas.
- Estadísticas básicas por estado y categoría.
- WebSocket para actualizar mapa, tabla y estadísticas sin recargar.
- Docker, HTTPS, backups y una base de datos administrada.

### Fuera del MVP

- Aplicación iOS.
- Recuperación de contraseña, login social y edición avanzada del perfil del vecino.
- Vinculación posterior de un reporte anónimo con una cuenta.
- Notificaciones push móviles.
- Chat entre vecino y municipio.
- Planificación avanzada de recorridos, turnos y capacidad de cuadrillas.
- Asignación automática basada en ubicación, carga o inteligencia artificial.
- Priorización automática o inteligencia artificial.
- Analítica histórica avanzada y reportes exportables complejos.
- Multi-tenancy para varias ciudades en una misma instalación.

La asignación manual y la prioridad operativa sí forman parte del MVP. Se excluyen la optimización automática de recorridos, el balanceo inteligente de carga y la planificación detallada de cuadrillas.

## Requisitos normalizados

### Funcionales

| ID | Requisito |
|---|---|
| RF-01 | Android debe mostrar reportes geolocalizados sobre un mapa. |
| RF-02 | El vecino debe poder iniciar un reporte seleccionando un punto del mapa. |
| RF-03 | El formulario debe capturar título, categoría, descripción, ubicación y foto opcional. |
| RF-04 | El vecino debe poder consultar el detalle y el estado de un reporte. |
| RF-05 | Android y el panel deben filtrar por categoría y estado. |
| RF-06 | El administrador debe cambiar el estado entre `pending`, `in_progress` y `resolved`. |
| RF-07 | El panel debe mostrar una tabla paginada y un mapa con los reportes. |
| RF-08 | El panel debe mostrar totales y distribuciones por categoría y estado. |
| RF-09 | El backend debe almacenar el binario y los metadatos de cada fotografía en PostgreSQL. |
| RF-10 | El administrador debe poder eliminar un reporte con confirmación y auditoría. |
| RF-11 | El servidor debe generar un `tracking_code` para cada reporte anónimo. |
| RF-12 | El vecino debe consultar el estado usando solo el código de seguimiento. |
| RF-13 | El backend debe publicar eventos por WebSocket después de una transacción confirmada. |
| RF-14 | Ktor debe servir el panel administrativo desde el mismo despliegue del backend. |
| RF-15 | El administrador debe poder asignar o reasignar un reporte a un equipo o responsable. |
| RF-16 | El administrador debe poder establecer la prioridad y una fecha objetivo opcional. |
| RF-17 | Cada cambio de asignación o prioridad debe conservar actor, fecha y valor anterior. |
| RF-18 | El vecino debe poder registrarse, iniciar sesión, consultar su sesión y cerrarla. |
| RF-19 | El vecino autenticado debe poder listar y consultar los reportes creados con su cuenta. |
| RF-20 | El alta debe permitir elegir explícitamente entre reporte asociado a la cuenta y reporte anónimo. |
| RF-21 | El backend debe obtener el autor registrado desde la sesión y nunca aceptar un `userId` enviado por el cliente. |

### No funcionales

| ID | Criterio verificable |
|---|---|
| RNF-01 | El mapa debe renderizar hasta 200 reportes en menos de 3 segundos en condiciones de prueba definidas. |
| RNF-02 | El flujo Android debe funcionar desde 360 dp y el panel desde 375 px sin scroll horizontal accidental. |
| RNF-03 | El panel debe exigir credenciales válidas y usar cookies de sesión seguras. |
| RNF-04 | Los endpoints CRUD deben alcanzar un p95 menor a 500 ms bajo carga normal acordada. |
| RNF-05 | Las imágenes deben aceptar JPG, PNG o WebP, con máximo de 5 MB, validación MIME y firma del archivo. |
| RNF-06 | Una transacción confirmada debe sobrevivir a un reinicio inesperado sin corrupción. |
| RNF-07 | Node.js/npm solo deben utilizarse para desarrollar y compilar Angular; Node.js no debe ejecutarse como runtime ni servidor en producción. |
| RNF-08 | Las consultas de mapa deben usar índices espaciales PostGIS y limitar el área o cantidad solicitada. |
| RNF-09 | Toda comunicación externa debe usar HTTPS; el WebSocket debe usar WSS en producción. |
| RNF-10 | La interfaz no debe usar el color como único indicador y debe mantener navegación por teclado en el panel. |
| RNF-11 | Los cambios concurrentes de estado, asignación o prioridad deben detectar versiones obsoletas y evitar sobrescrituras silenciosas. |
| RNF-12 | Las consultas de listados y mapa nunca deben cargar la columna binaria de imágenes; el contenido se obtiene mediante un endpoint específico. |
| RNF-13 | Contraseñas y tokens de sesión nunca se almacenan en texto plano ni aparecen en respuestas, logs o auditoría. |
| RNF-14 | Las respuestas públicas y eventos WebSocket nunca exponen correo, identificador de cuenta ni otro dato personal del autor. |

## Criterios de éxito del MVP

- Un vecino puede registrarse, iniciar sesión y crear un reporte asociado a su cuenta desde Android.
- El vecino autenticado puede consultar sus reportes sin introducir códigos de seguimiento.
- Un vecino puede elegir crear un reporte anónimo en menos de tres minutos.
- Para un reporte anónimo, el vecino recibe un código copiable y puede consultar el estado sin registrarse.
- Un administrador puede localizar, filtrar y actualizar un reporte desde `/admin`.
- Un administrador puede delegar un reporte, cambiar su prioridad y recuperar el historial de esos cambios.
- El cambio de estado se refleja en Android y en el panel sin recargar.
- La fotografía se guarda en `report_images` y puede recuperarse con autorización, tipo MIME y checksum correctos.
- El historial y la auditoría permiten explicar quién cambió un estado o eliminó un reporte.
- El sistema puede restaurarse desde un backup probado.

## Preguntas que deben cerrarse antes de programar

1. ¿Qué ciudad y zona geográfica se usarán para las pruebas iniciales?
2. ¿Qué proveedor administrará PostgreSQL + PostGIS?
3. ¿Qué volumen inicial y crecimiento mensual de imágenes debe soportar PostgreSQL?
4. ¿Qué SDK de mapas se aprobará para Android? Recomendación a evaluar: MapLibre con un proveedor de tiles compatible.
5. ¿El municipio requiere una segunda lengua o solo español en el MVP?
6. ¿Qué política de retención se aplicará a reportes, fotografías y auditoría?
7. ¿Qué equipos municipales y administradores se cargarán inicialmente?
8. ¿Qué reglas municipales determinarán el uso de `urgent` y las fechas objetivo?
9. ¿Se exigirá verificar el correo antes de crear reportes registrados?
10. ¿Qué política de baja, anonimización y retención se aplicará a las cuentas de vecinos?

## Revisión de interfaz Stitch — 2026-09-08

Se adopta la interfaz ciudadana MuniReport del proyecto Stitch Mapa-Urbano como referencia visual, usando Mapa y Mis reportes interactivos. Se conservan estética y alcance del MVP; se autorizan conexiones, retornos, scroll universal, animaciones discretas y correcciones de textos funcionales. Ver [especificación de UX](05-ux-e-interfaces.md) e [informe de verificación](13-verificacion-stitch.md).

No se incorporan login social, biometría, recuperación de contraseña, edición avanzada de perfil, push, votos, actas, telemetría de cuadrillas ni métricas vecinales avanzadas. Sus controles conservan su lugar pero abren un aviso con retorno; no simulan éxito. DNI, teléfono y distrito no se recogen ni exigen. Términos, política municipal, área operativa y contacto de asistencia siguen pendientes de definición; los ejemplos geográficos y 147 no son configuración de producción.

| ID | Requisito de interfaz añadido, sin ampliar API |
|---|---|
| UX-01 | Cada pantalla y diálogo permite scroll cuando el contenido desborda, también con teclado y texto ampliado. |
| UX-02 | Todo destino operativo tiene acceso y retorno interno, conservando filtros, selección, scroll y borrador donde corresponda. |
| UX-03 | La confirmación y el detalle muestran el mismo reporte seleccionado/creado; código únicamente en anónimos. |
| UX-04 | Las acciones futuras informan su indisponibilidad; no generan cambios ni éxitos ficticios. |
| UX-05 | Las transiciones respetan movimiento reducido y no alteran el aspecto de las pantallas. |

La existencia de un diseño o una respuesta de generación no acredita implementación. El estado comprobado del prototipo y los casos Android pendientes están separados en el informe.
