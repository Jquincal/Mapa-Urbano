# Seguridad

## Reporte responsable

No publiques vulnerabilidades, credenciales, códigos de seguimiento reales ni datos personales en issues públicos. Comunica el hallazgo de forma privada al jefe de proyecto hasta que el repositorio disponga de un canal formal de seguridad.

## Datos prohibidos en Git

- Contraseñas, tokens, claves privadas y secretos de CI.
- Archivos `.env` reales, `local.properties`, keystores o cuentas de servicio.
- Fotografías o reportes reales de vecinos.
- Dumps de PostgreSQL o copias de buckets.
- Logs con sesiones, cookies, códigos de seguimiento o URLs firmadas.

Los ejemplos deben utilizar valores ficticios y no reutilizables.

## Alcance mínimo de revisión

Los cambios que afecten autenticación, autorización, uploads, rate limits, CSRF, WebSocket, S3, migraciones o datos sensibles requieren revisión de Juan y Joaquín, además del responsable del área.
