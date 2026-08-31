# database

Directorio reservado para migraciones, datos iniciales y documentación de PostgreSQL + PostGIS.

El modelo lógico y sus índices están definidos en [docs/03-modelo-de-datos.md](../docs/03-modelo-de-datos.md). Las fotografías se almacenarán en PostgreSQL como `BYTEA` dentro de `report_images`, acompañadas por tipo MIME, tamaño, checksum y dimensiones.

El esquema separa `users` (vecinos) de `admin_users` (personal municipal). `reports.user_id` es nullable: los reportes registrados lo obtienen desde la sesión y los anónimos usan exclusivamente un código de seguimiento hasheado.

Un ejemplo de entidades y DDL inicial está en [docs/12-entidades-bd-ejemplo.md](../docs/12-entidades-bd-ejemplo.md).
