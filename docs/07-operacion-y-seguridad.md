# Operación y seguridad

## Objetivo

La plataforma maneja cuentas de vecinos, ubicaciones, fotografías y acciones administrativas. La seguridad debe estar presente desde el registro y el primer reporte.

## Controles por superficie

| Superficie | Riesgo principal | Control mínimo |
|---|---|---|
| Alta pública | Spam, abuso y contenido malicioso | Rate limit, validación, límites de payload y moderación operativa. |
| Registro/login de vecino | Enumeración, fuerza bruta o credenciales robadas | Mensajes genéricos, hash adaptativo, rate limit y sesiones revocables. |
| Sesión Android | Robo o reproducción del token | Token opaco con hash en servidor, expiración, revocación y almacenamiento seguro del dispositivo. |
| Código de seguimiento | Acceso no autorizado a un reporte | Código aleatorio, alta entropía, hash/HMAC, mensajes genéricos y rate limit. |
| Login admin | Fuerza bruta o robo de sesión | Hash adaptativo, rate limit, cookie segura, expiración y logout. |
| Fotografía | Malware, EXIF o crecimiento inesperado | Validación binaria, 5 MB, dimensiones máximas, checksum, `BYTEA` y endpoint autorizado. |
| API | Inyección y exposición de datos | Queries parametrizadas, validación de entrada y serializadores explícitos. |
| WebSocket | Fuga de datos o agotamiento de recursos | Canales separados, autenticación admin, límites, heartbeat y origen validado. |
| Base de datos | Pérdida o acceso excesivo | Proveedor administrado, mínimo privilegio, backups y TLS. |
| Panel | CSRF, XSS y acciones accidentales | Mismo origen, protección CSRF, escape de contenido, confirmaciones y auditoría. |

## Código de seguimiento anónimo

- Generar con un generador criptográficamente seguro.
- No usar un ID secuencial ni incluir fecha, coordenadas o datos del vecino.
- Usar un formato legible, por ejemplo grupos alfanuméricos separados por guiones.
- Guardar solo una representación no reversible apropiada para consulta.
- Nunca escribir el código completo en logs, analítica o auditoría.
- Aplicar rate limit por IP y, si se dispone, por huella de abuso no identificatoria.
- Devolver un mensaje genérico tanto para código inválido como para reporte no disponible.
- Definir antes del lanzamiento una política de expiración o permanencia.

El código funciona como un bearer token: quien lo posee puede consultar el reporte. La interfaz debe explicarlo sin lenguaje técnico.

## Cuentas y sesiones de vecinos

- Normalizar el correo y aplicar unicidad sin distinguir mayúsculas.
- Guardar contraseñas con un hash adaptativo aprobado y parámetros actualizables.
- Entregar a Android un token opaco aleatorio; guardar únicamente `token_hash` en `user_sessions`.
- Conservar el token en almacenamiento seguro del dispositivo y enviarlo solo por HTTPS como Bearer.
- Aplicar expiración absoluta, expiración por inactividad y revocación en logout o baja.
- Derivar `reports.user_id` de la sesión; rechazar cualquier `userId` recibido en el alta.
- En modo anónimo, no guardar `user_id` ni otra referencia de cuenta aunque exista sesión.
- No exponer correo, nombre o identificador del autor en REST público, WebSocket, logs o analítica.
- Mantener separadas las sesiones de vecinos y administradores; ninguna credencial de vecino habilita `/api/v1/admin/*`.

## Sesiones administrativas

- Cookie `HttpOnly`, `Secure` y `SameSite=Lax`.
- Expiración por inactividad y expiración absoluta.
- Rotación del identificador de sesión después del login.
- Invalidación en logout y al desactivar el usuario.
- Protección CSRF para `POST`, `PATCH` y `DELETE`.
- Mensajes de login genéricos y eventos de login auditados.
- No permitir credenciales por defecto en producción.

## Fotografía en PostgreSQL

- Guardar el binario validado en `report_images.data BYTEA`, nunca como Base64.
- Confirmar reporte, imagen y metadatos dentro de una única transacción.
- Validar tamaño antes de procesar, MIME real, firma binaria y dimensiones después de decodificar.
- Aceptar solo JPG, PNG y WebP, según límites aprobados.
- Eliminar metadatos EXIF sensibles cuando la política de privacidad lo requiera.
- Entregar el archivo mediante un endpoint autorizado con `Content-Type`, `Content-Length`, `ETag` y `nosniff`.
- Excluir la columna binaria de listados, mapa, estadísticas, auditoría y logs.
- Registrar checksum, tamaño y dimensiones; nunca registrar el contenido.
- Definir retención y borrado coordinado con la política del reporte.
- Monitorizar tamaño de la base, crecimiento, I/O, latencia, backup y restauración.

## Red y despliegue

- HTTPS obligatorio en producción; WebSocket usa WSS.
- Red privada para PostgreSQL cuando el proveedor lo permita.
- Lista de orígenes permitidos; evitar `*` en producción.
- Reverse proxy configurado con headers reenviados confiables.
- Imagen Docker mínima y actualizada, sin herramientas de desarrollo.
- Usuario de proceso sin privilegios de root cuando sea compatible.
- Secretos en un gestor de secretos o variables protegidas del proveedor.
- No guardar secretos en Markdown, commits, imágenes ni logs.

## Backups y continuidad

Objetivos propuestos, pendientes de aprobación:

| Elemento | Objetivo inicial |
|---|---|
| Base de datos | Backup automático diario y recuperación a un punto en el tiempo si el proveedor lo permite. |
| Fotografías | Incluidas en el backup de PostgreSQL y verificadas durante la restauración. |
| RPO | Hasta 24 horas para MVP, salvo exigencia municipal diferente. |
| RTO | Hasta 4 horas para MVP, sujeto al proveedor. |
| Prueba de restauración | Mensual en un entorno separado y después de cambios de infraestructura. |

Un backup no se considera válido hasta restaurarlo. El runbook debe incluir credenciales, orden de recuperación, migraciones y verificación de integridad.

## Observabilidad

### Métricas

- Latencia p50/p95/p99 por ruta.
- Errores 4xx/5xx por endpoint.
- Tiempo de validación e inserción de imágenes en PostgreSQL.
- Latencia y bytes transferidos por el endpoint de imágenes.
- Cantidad de reportes por estado.
- Rechazos de fotografías por motivo.
- Conexiones activas, reconexiones y errores WebSocket.
- Uso de CPU, memoria, pool de conexiones y almacenamiento.
- Duración de consultas geoespaciales.

### Logs

Cada request debe incluir `requestId`, ruta, método, código y duración. No registrar:

- Contraseñas.
- Correos completos salvo en un contexto operativo explícitamente autorizado.
- Tokens de sesión de vecinos o administradores.
- Códigos de seguimiento completos.
- Hashes de contraseñas.
- Contenido binario o representaciones Base64.
- Cuerpo completo de descripciones o notas internas.
- Fotografías.

## Pruebas de seguridad antes de producción

- Validación de autenticación, autorización y expiración de sesión.
- Registro duplicado, enumeración de correo, revocación y acceso cruzado entre cuentas.
- Intento de enviar un `userId` ajeno y verificación de que el backend lo ignora o rechaza.
- Reporte anónimo creado con sesión activa y confirmación de que no queda asociado a la cuenta.
- Intentos repetidos de login y consulta por código.
- CSRF en todas las mutaciones con cookie.
- XSS con títulos, descripciones y nombres de archivo maliciosos.
- Inyección SQL mediante campos de búsqueda y filtros.
- Archivos falsos, polyglots, MIME alterado y payloads superiores a 5 MB.
- Acceso al endpoint de imagen sin visibilidad pública o permiso administrativo.
- Confirmación de que listados y estadísticas no seleccionan la columna `BYTEA`.
- WebSocket sin sesión en canal admin.
- Bypass de filtros geográficos y paginación ilimitada.
- Verificación de que cada cambio produce historial y auditoría.
