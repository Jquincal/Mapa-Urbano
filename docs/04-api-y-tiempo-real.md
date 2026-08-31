# API REST y tiempo real

## Convenciones generales

- Prefijo: `/api/v1` para todos los endpoints de negocio y autenticación.
- JSON en camelCase en las respuestas y requests.
- Fechas en ISO 8601 UTC.
- UUID en formato textual.
- Errores con código estable y mensaje accionable.
- Paginación por cursor para el panel; límite explícito para mapa.
- La API nunca devuelve contraseñas, hashes, códigos completos ajenos ni imágenes embebidas en JSON.

## Formato de error

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Revisa los datos enviados.",
    "details": [
      { "field": "description", "reason": "required" }
    ],
    "requestId": "01J..."
  }
}
```

Los mensajes de autenticación y seguimiento no deben confirmar si un usuario, reporte o código existe.

## Endpoints públicos

| Método y ruta | Propósito | Respuesta esperada |
|---|---|---|
| `GET /api/v1/categories` | Listar categorías activas. | `200` con catálogo. |
| `GET /api/v1/reports?bbox=...&status=...&category=...` | Obtener marcadores dentro del área visible. | `200` paginado o limitado. |
| `GET /api/v1/reports/{id}` | Ver detalle público de un reporte. | `200`, `404` genérico si no está disponible. |
| `GET /api/v1/reports/{id}/image` | Obtener la imagen de un reporte públicamente visible. | `200` binario, `404` genérico si no está disponible. |
| `POST /api/v1/reports` | Crear reporte anónimo; acepta `multipart/form-data`. | `201` con código completo una sola vez. |
| `POST /api/v1/report-status` | Consultar estado usando `trackingCode` en el cuerpo. | `200` con estado e historial público. |
| `GET /health/live` | Verificar proceso. | `200` si el proceso vive. |
| `GET /health/ready` | Verificar dependencias esenciales. | `200` o `503`. |

### Alta de reporte

Campos mínimos de `multipart/form-data`:

| Campo | Tipo | Requerido |
|---|---|---|
| `title` | texto | Sí |
| `description` | texto | Sí |
| `categorySlug` | texto | Sí |
| `latitude` | decimal | Sí |
| `longitude` | decimal | Sí |
| `photo` | archivo | No |

Respuesta conceptual:

```json
{
  "id": "4f4a2c86-4a2b-4ad5-86d9-3cf4f2c7a100",
  "trackingCode": "7F2K-9B1M-4X3P",
  "status": "pending",
  "createdAt": "2026-08-26T21:30:00Z"
}
```

El cliente debe mostrar el código con acción de copiar y una advertencia: si se pierde, no existe una cuenta de vecino para recuperarlo en el MVP.

### Consulta por seguimiento

Request conceptual:

```json
{
  "trackingCode": "7F2K-9B1M-4X3P"
}
```

La respuesta incluye solo información pública: estado actual, categoría, ubicación aproximada o exacta según la política, fechas e historial visible. Puede incluir `hasImage` y la ruta del endpoint binario, pero nunca Base64, identidad del administrador ni notas internas.

## Endpoints administrativos

| Método y ruta | Propósito | Autorización |
|---|---|---|
| `POST /api/v1/auth/login` | Crear sesión del administrador. | Pública con rate limit. |
| `POST /api/v1/auth/logout` | Invalidar sesión. | Sesión válida. |
| `GET /api/v1/auth/me` | Consultar administrador actual. | Sesión válida. |
| `GET /api/v1/admin/reports` | Tabla paginada con filtros por estado, categoría, prioridad, equipo, responsable y fecha objetivo. | Sesión válida. |
| `GET /api/v1/admin/reports/{id}` | Detalle completo y metadatos de imagen. | Sesión válida. |
| `GET /api/v1/admin/reports/{id}/image` | Obtener la imagen aunque el reporte no sea público. | Sesión válida y permiso. |
| `PATCH /api/v1/admin/reports/{id}/status` | Cambiar estado y registrar historial. | Sesión válida. |
| `PATCH /api/v1/admin/reports/{id}/priority` | Cambiar prioridad o fecha objetivo con control de versión. | Sesión válida y permiso. |
| `PUT /api/v1/admin/reports/{id}/assignment` | Asignar o reasignar a equipo/responsable. | Sesión válida y permiso. |
| `DELETE /api/v1/admin/reports/{id}/assignment` | Retirar la asignación activa. | Sesión válida y permiso. |
| `GET /api/v1/admin/teams` | Listar equipos e integrantes activos. | Sesión válida. |
| `POST /api/v1/admin/teams` | Crear un equipo municipal. | Sesión válida y permiso. |
| `GET /api/v1/admin/teams/{id}` | Consultar un equipo y sus integrantes. | Sesión válida. |
| `PATCH /api/v1/admin/teams/{id}` | Editar o desactivar un equipo. | Sesión válida y permiso. |
| `PUT /api/v1/admin/teams/{teamId}/members/{adminUserId}` | Incorporar un administrador a un equipo. | Sesión válida y permiso. |
| `DELETE /api/v1/admin/teams/{teamId}/members/{adminUserId}` | Retirar un administrador de un equipo. | Sesión válida y permiso. |
| `GET /api/v1/admin/assignees?teamId=...` | Listar responsables posibles, opcionalmente por equipo. | Sesión válida. |
| `GET /api/v1/admin/categories` | Listar todas las categorías para administrarlas. | Sesión válida. |
| `POST /api/v1/admin/categories` | Crear una categoría. | Sesión válida y permiso. |
| `PATCH /api/v1/admin/categories/{id}` | Editar o desactivar una categoría. | Sesión válida y permiso. |
| `DELETE /api/v1/admin/reports/{id}` | Dar de baja un reporte con auditoría. | Sesión válida y permiso. |
| `GET /api/v1/admin/statistics` | Totales y agregaciones. | Sesión válida. |
| `GET /api/v1/admin/audit` | Consultar auditoría filtrada. | Sesión válida y permiso. |

Los cambios de estado, prioridad y asignación reciben la `version` conocida por el cliente. Si otro administrador modificó el reporte, la API devuelve `409 CONFLICT` y el panel recarga el detalle antes de reintentar. El cambio de estado debe ser idempotente cuando se repite el mismo valor o devolver un error de transición clara. Una eliminación exige confirmación en la interfaz y una segunda validación en el servidor.

### Delegación y prioridad

Asignación conceptual:

```json
{
  "teamId": "4f1de213-3862-4cdf-b648-3a73502c0fef",
  "responsibleAdminUserId": "ac99b913-cbf6-4fbe-a76c-2fdd8f067644",
  "version": 3
}
```

Prioridad conceptual:

```json
{
  "priority": "high",
  "dueAt": "2026-09-02T18:00:00Z",
  "version": 4
}
```

Las respuestas devuelven el reporte actualizado, su nueva `version` y el resumen de historial necesario para refrescar la interfaz.

### Entrega de imágenes

Los endpoints de imagen leen `report_images.data` solo después de comprobar que el reporte es visible o que la sesión tiene permiso. La respuesta no es JSON:

```http
HTTP/1.1 200 OK
Content-Type: image/jpeg
Content-Length: 184233
ETag: "sha256-del-contenido"
X-Content-Type-Options: nosniff
Cache-Control: private, max-age=300
```

El backend admite `If-None-Match` para responder `304` cuando el checksum no cambió. Los endpoints de mapa, tabla, estadísticas e historial nunca cargan la columna `BYTEA`.

## WebSocket

### Canales

| Ruta | Consumidores | Datos |
|---|---|---|
| `/api/v1/ws/public` | Android y cualquier vista pública autorizada. | Marcadores y estados sin datos internos. |
| `/api/v1/ws/admin` | Panel administrativo. | Cambios operativos y estadísticas. |

El canal administrativo exige sesión. El canal público tiene límites de conexión y solo envía datos que ya son visibles públicamente.

### Eventos

```json
{
  "type": "report.updated",
  "occurredAt": "2026-08-26T21:31:00Z",
  "payload": {
    "id": "4f4a2c86-4a2b-4ad5-86d9-3cf4f2c7a100",
    "status": "in_progress",
    "categorySlug": "luminaria",
    "latitude": -32.8895,
    "longitude": -68.8458,
    "updatedAt": "2026-08-26T21:31:00Z"
  }
}
```

Tipos mínimos: `connected`, `report.created`, `report.updated`, `report.assignment_changed`, `report.priority_changed`, `report.deleted`, `statistics.updated`, `heartbeat` y `error`.

### Reglas de sincronización

- REST carga el estado inicial.
- WebSocket aplica cambios posteriores.
- Cada evento tiene `occurredAt` y un identificador de evento si se implementa outbox.
- Tras una desconexión, el cliente reconsulta REST antes de continuar aplicando eventos.
- Los clientes usan reconexión exponencial con tope y muestran un estado no intrusivo de conexión.
- El servidor publica después del commit; una conexión caída no revierte el negocio.
- Los eventos administrativos de asignación y prioridad no se publican en el canal público.

## Límites y seguridad de API

- Rate limit para alta de reportes, consulta por código, login y subida de archivos.
- Límite de tamaño de request multipart menor o igual al límite operativo acordado.
- CORS restringido; el panel servido por Ktor usa mismo origen.
- Protección CSRF para operaciones basadas en cookie.
- Validación de `Origin`/`Host` en WebSocket cuando corresponda.
- `requestId` en logs y respuestas para soporte.
- No registrar cuerpos completos de reportes ni códigos de seguimiento.
