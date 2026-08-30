# Operación y seguridad

## Objetivo

La plataforma maneja ubicaciones, fotografías y acciones administrativas. La seguridad debe estar presente desde el primer endpoint, aunque el MVP no tenga cuentas de vecinos.

## Controles por superficie

| Superficie | Riesgo principal | Control mínimo |
|---|---|---|
| Alta pública | Spam, abuso y contenido malicioso | Rate limit, validación, límites de payload y moderación operativa. |
| Código de seguimiento | Acceso no autorizado a un reporte | Código aleatorio, alta entropía, hash/HMAC, mensajes genéricos y rate limit. |
| Login admin | Fuerza bruta o robo de sesión | Hash adaptativo, rate limit, cookie segura, expiración y logout. |
| Fotografía | Malware, EXIF o costos inesperados | Bucket privado, validación binaria, 5 MB, checksum, URL firmada y limpieza. |
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

## Sesiones administrativas

- Cookie `HttpOnly`, `Secure` y `SameSite=Lax`.
- Expiración por inactividad y expiración absoluta.
- Rotación del identificador de sesión después del login.
- Invalidación en logout y al desactivar el usuario.
- Protección CSRF para `POST`, `PATCH` y `DELETE`.
- Mensajes de login genéricos y eventos de login auditados.
- No permitir credenciales por defecto en producción.

## Fotografía y almacenamiento S3

- Bucket privado; no se sirve por URL pública permanente.
- Claves generadas por el backend, sin nombres ni rutas controladas por el cliente.
- Validar tamaño antes de procesar y MIME real después de recibir.
- Aceptar solo JPG, PNG y WebP, según límites aprobados.
- Considerar eliminación de metadatos EXIF si la política de privacidad lo requiere.
- Usar URLs firmadas de corta duración para administradores autorizados.
- Registrar checksum y tamaño; no registrar el contenido.
- Definir ciclo de vida, retención y proceso de borrado.
- Monitorizar costos y cantidad de objetos.

## Red y despliegue

- HTTPS obligatorio en producción; WebSocket usa WSS.
- Red privada para PostgreSQL y S3 cuando el proveedor lo permita.
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
| Fotografías | Versionado o política de recuperación según costo y privacidad. |
| RPO | Hasta 24 horas para MVP, salvo exigencia municipal diferente. |
| RTO | Hasta 4 horas para MVP, sujeto al proveedor. |
| Prueba de restauración | Mensual en un entorno separado y después de cambios de infraestructura. |

Un backup no se considera válido hasta restaurarlo. El runbook debe incluir credenciales, orden de recuperación, migraciones y verificación de integridad.

## Observabilidad

### Métricas

- Latencia p50/p95/p99 por ruta.
- Errores 4xx/5xx por endpoint.
- Tiempo de alta incluyendo almacenamiento S3.
- Cantidad de reportes por estado.
- Rechazos de fotografías por motivo.
- Conexiones activas, reconexiones y errores WebSocket.
- Uso de CPU, memoria, pool de conexiones y almacenamiento.
- Duración de consultas geoespaciales.

### Logs

Cada request debe incluir `requestId`, ruta, método, código y duración. No registrar:

- Contraseñas.
- Códigos de seguimiento completos.
- Hashes de contraseñas.
- URLs firmadas.
- Cuerpo completo de descripciones o notas internas.
- Fotografías.

## Pruebas de seguridad antes de producción

- Validación de autenticación, autorización y expiración de sesión.
- Intentos repetidos de login y consulta por código.
- CSRF en todas las mutaciones con cookie.
- XSS con títulos, descripciones y nombres de archivo maliciosos.
- Inyección SQL mediante campos de búsqueda y filtros.
- Archivos falsos, polyglots, MIME alterado y payloads superiores a 5 MB.
- Acceso directo a objetos S3 sin firma.
- WebSocket sin sesión en canal admin.
- Bypass de filtros geográficos y paginación ilimitada.
- Verificación de que cada cambio produce historial y auditoría.
