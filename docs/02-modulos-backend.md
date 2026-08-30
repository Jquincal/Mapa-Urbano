# Módulos del backend

## Convenciones

- Paquetes y nombres internos en inglés; textos visibles en español.
- Cada módulo expone casos de uso, no repositorios directamente.
- Las operaciones que cambian más de una entidad se ejecutan dentro de una transacción.
- Los eventos se publican después del commit.
- Las respuestas públicas nunca incluyen credenciales, hashes, rutas internas de S3 ni datos de auditoría.

## Mapa de módulos

| Módulo | Responsabilidad MVP | Dependencias principales |
|---|---|---|
| `auth` | Login, logout, sesión y autorización de administradores | `audit`, `shared` |
| `reports` | Crear, listar, consultar, priorizar y cambiar estado de reportes | `categories`, `media`, `notifications`, `audit` |
| `assignments` | Equipos, integrantes, asignación y reasignación de reportes | `auth`, `reports`, `notifications`, `audit` |
| `categories` | Catálogo activo de tipos de problema | `shared` |
| `media` | Validar imágenes, escribir en S3 y generar URL firmada | `shared` |
| `statistics` | Totales y agregaciones para el panel | `reports`, `categories` |
| `notifications` | Canales WebSocket y distribución de eventos sanitizados | `reports`, `statistics`, `shared` |
| `audit` | Registro de acciones administrativas y eventos de seguridad | `shared` |

## `auth`

### Casos de uso

- Iniciar sesión con usuario y contraseña.
- Crear, desactivar o rotar credenciales de administrador mediante una operación operativa segura.
- Consultar la sesión actual.
- Cerrar sesión e invalidar la sesión.

### Reglas

- Las contraseñas se guardan con un algoritmo de hash adaptativo aprobado, nunca en texto plano.
- La sesión del panel usa cookie `HttpOnly`, `Secure` y `SameSite=Lax`.
- Los mensajes de login fallido son genéricos.
- Se aplica rate limit por IP y usuario lógico.
- Las operaciones administrativas requieren una sesión activa y un permiso explícito.

## `reports`

### Casos de uso

- Listar reportes por ventana geográfica, categoría, estado y cursor.
- Obtener el detalle público de un reporte.
- Crear un reporte anónimo con código de seguimiento.
- Consultar un reporte por código de seguimiento sin revelar si otro código existe.
- Obtener el detalle administrativo.
- Cambiar el estado con historial.
- Cambiar la prioridad y la fecha objetivo con historial.
- Eliminar lógicamente o físicamente según la política aprobada; el MVP debe conservar la auditoría.

### Reglas de negocio

- Estado inicial: `pending`.
- Prioridad inicial: `medium`; valores permitidos: `low`, `medium`, `high` y `urgent`.
- La fecha objetivo es opcional y solo puede ser modificada por un administrador autorizado.
- Transiciones MVP permitidas: `pending → in_progress → resolved`; se debe decidir si se habilita volver atrás.
- Título: 1–150 caracteres.
- Descripción: longitud mínima y máxima a definir antes de la migración.
- La coordenada debe estar dentro del área operativa configurada.
- El código de seguimiento es aleatorio, no contiene el ID y se muestra al vecino una sola vez en la respuesta de alta.
- Un mismo reporte puede tener cero o más attachments; la primera versión admite como máximo una fotografía.

## `assignments`

### Casos de uso

- Crear, editar, desactivar y listar equipos municipales.
- Asociar administradores activos a uno o más equipos.
- Asignar un reporte a un equipo, a un responsable individual o a ambos.
- Reasignar o retirar una asignación sin perder el historial anterior.
- Filtrar la cola administrativa por equipo, responsable, prioridad y fecha objetivo.

### Reglas

- Un reporte puede tener como máximo una asignación activa.
- El equipo y el responsable son opcionales por separado, pero una asignación activa debe indicar al menos uno.
- El responsable individual debe pertenecer al equipo cuando ambos valores estén presentes.
- Cada cambio registra quién lo realizó, la asignación anterior, la nueva y la fecha.
- Desactivar un equipo o usuario no elimina el historial y exige resolver sus asignaciones activas.
- La API debe detectar actualizaciones concurrentes para no sobrescribir una reasignación reciente.

## `categories`

El catálogo inicial conserva las categorías del documento original:

| Slug | Nombre visible | Color de referencia |
|---|---|---|
| `bache` | Bache | Rojo/naranja |
| `luminaria` | Luminaria | Azul |
| `basura` | Basura | Gris |
| `vandalismo` | Vandalismo | Violeta |
| `inundacion` | Inundación | Azul oscuro |
| `otro` | Otro | Verde azulado |

El color es una ayuda visual. El nombre, el ícono y el estado textual deben comunicar el significado sin depender solo del color.

## `media`

### Flujo de fotografía

1. Recibir el archivo como parte del alta del reporte.
2. Rechazar temprano si excede 5 MB o la extensión no coincide con el formato permitido.
3. Validar MIME real y firma binaria; el nombre enviado por el cliente no es confiable.
4. Generar un `object_key` interno que no use el nombre original.
5. Subir al bucket privado S3-compatible.
6. Guardar metadatos en `attachments` dentro de la transacción del reporte.
7. Entregar una URL firmada solo cuando el consumidor tenga autorización.

### Reglas

- Permitidos: JPG, PNG y WebP.
- Límite: 5 MB por archivo.
- No se ejecuta contenido activo desde el bucket.
- Se debe eliminar EXIF sensible si el tratamiento de privacidad lo exige.
- La URL firmada debe expirar y no debe aparecer en logs.

## `statistics`

Entrega el total de reportes y las distribuciones por estado, categoría, prioridad, equipo y responsable. Las consultas deben usar agregaciones indexadas y parámetros de fecha opcionales.

En el MVP no se define un almacén analítico separado. Si la carga crece, se evaluará una vista materializada o una réplica de lectura.

## `notifications`

En la primera versión, este módulo gestiona eventos en tiempo real:

- `report.created` para nuevos marcadores.
- `report.updated` para cambios de estado o edición administrativa.
- `report.assignment_changed` para asignaciones y reasignaciones administrativas.
- `report.priority_changed` para prioridad o fecha objetivo.
- `report.deleted` para retirar un marcador.
- `statistics.updated` para actualizar métricas.

Las notificaciones push móviles quedan fuera del MVP. El módulo se diseña para poder incorporar otro canal más adelante sin cambiar el dominio de reportes.

## `audit`

Registra como mínimo:

- Login exitoso y fallido.
- Logout.
- Creación, cambio de estado y eliminación de reportes.
- Cambios de asignación, prioridad y fecha objetivo.
- Alta o desactivación de categorías.
- Errores de autorización relevantes.

Cada evento incluye actor, acción, entidad, identificador, fecha, IP normalizada y metadatos mínimos. No debe registrar contraseñas, códigos de seguimiento ni URLs firmadas.

## Contratos entre módulos

| Evento | Productor | Consumidores | Momento |
|---|---|---|---|
| `ReportCreated` | `reports` | `notifications`, `statistics`, `audit` | Después del commit |
| `ReportStatusChanged` | `reports` | `notifications`, `statistics`, `audit` | Después del commit |
| `ReportPriorityChanged` | `reports` | `notifications`, `statistics`, `audit` | Después del commit |
| `ReportAssignmentChanged` | `assignments` | `notifications`, `statistics`, `audit` | Después del commit |
| `ReportDeleted` | `reports` | `notifications`, `statistics`, `audit` | Después del commit |
| `AdminAuthenticated` | `auth` | `audit` | Después de crear sesión |

Si la entrega de WebSocket falla, no se revierte la transacción de negocio. El cliente vuelve a sincronizar por REST al reconectarse.
