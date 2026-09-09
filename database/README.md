# PostgreSQL y PostGIS

Las migraciones ejecutables están en [migrations](migrations). Gradle las incluye en
`db/migration` dentro del backend; esa es la única copia empaquetada que usa Flyway.

| Versión | Contenido |
|---|---|
| V1 | Doce tablas, PostGIS, enums, restricciones, índices y triggers de `updated_at`. |
| V2 | Las seis categorías iniciales de los módulos del backend, con `ON CONFLICT DO NOTHING`. |

Flyway conserva versiones y checksums en `flyway_schema_history`. Repetir la ejecución
no vuelve a crear tablas ni elimina datos. No modificar una migración aplicada: crear
una versión nueva. `clean` y el baseline automático están deshabilitados.

El modelo lógico y sus índices están definidos en [docs/03-modelo-de-datos.md](../docs/03-modelo-de-datos.md). Las fotografías se almacenarán en PostgreSQL como `BYTEA` dentro de `report_images`, acompañadas por tipo MIME, tamaño, checksum y dimensiones.

El esquema separa `users` (vecinos) de `admin_users` (personal municipal). `reports.user_id` es nullable: los reportes registrados lo obtienen desde la sesión y los anónimos usan exclusivamente un código de seguimiento hasheado.

Un ejemplo de entidades y DDL inicial está en [docs/12-entidades-bd-ejemplo.md](../docs/12-entidades-bd-ejemplo.md).

## Aplicar migraciones

1. Aprovisionar una base vacía, por ejemplo `mapa_urbano`, en PostgreSQL con PostGIS
   disponible. La prueba reproducible usa `postgis/postgis:16-3.5`.
2. Configurar `DATABASE_JDBC_URL` (por ejemplo `jdbc:postgresql://localhost:5432/mapa_urbano`),
   `DATABASE_USER` y `DATABASE_PASSWORD` en el entorno. No incluir credenciales en Git.
3. Desde `backend-ktor`, ejecutar `./gradlew migrateDatabase` antes de habilitar el backend.

La cuenta de migración debe poder crear los objetos y activar `postgis` y `pgcrypto`.
En un servicio administrado, pedir al proveedor que habilite esas extensiones cuando
la cuenta no tenga permisos. No se fija propietario, locale ni configuración de Windows.
La aplicación usará una cuenta con permisos limitados cuando se implemente la persistencia.
El comando de migración es independiente del arranque HTTP; los endpoints siguen siendo stubs.

Una base creada manualmente con los scripts del commit `d26fafb` necesita un plan de
adopción previo: respaldar y revisar el esquema y los datos. Este cambio no hace baseline
automático ni ofrece una conversión destructiva de bases existentes.

## Verificación

Desde `backend-ktor`:

```bash
./gradlew test                 # Pruebas de rutas, sin Docker
./gradlew integrationTest      # PostgreSQL/PostGIS descartable mediante Testcontainers
./gradlew check                # Ambas suites; requiere Docker
```

La suite de integración falla si Docker no está disponible; no omite pruebas silenciosamente.
Comprueba migración desde una base vacía, segunda ejecución sin pérdida de datos, semillas
idempotentes, unicidad de imagen, límites de binarios, autoría excluyente, sesiones,
asignaciones, baja lógica, auditoría, consultas espaciales y marcas de actualización.
También provoca una migración fallida para comprobar su rollback y restaura un `pg_dump`
en otra base descartable, verificando el binario y el historial de Flyway.
GitHub Actions ejecuta `check` y publica los informes de ambas suites.

## Contratos y decisiones

- Cada reporte admite como máximo una imagen de 5 MiB (5.242.880 bytes).
- Las categorías son `bache`, `luminaria`, `basura`, `vandalismo`, `inundacion` y `otro`.
  Los colores hexadecimales concretan los colores de referencia del documento; el seed
  conserva cambios administrativos existentes, incluidos nombre, color y activación.
- La normalización y validación completa del correo pertenecen al backend. La base exige
  correo no vacío, longitud máxima y unicidad sin distinguir mayúsculas.
- Los triggers actualizan `updated_at`; no incrementan `version`. Los casos de uso deben
  implementar el control optimista, validar equipos activos/pertenencia, registrar actores
  y revocar sesiones al desactivar cuentas, tal como exige el modelo documentado.
- `admin_sessions` depende del mecanismo de sesiones elegido; `outbox_events` y
  `notification_events` son opcionales. No se agregan cuentas ni contraseñas iniciales.
- La baja de reportes y vecinos es lógica. La eliminación física y la anonimización
  requieren la política de retención antes de producción.

Los diagramas se mantienen en Markdown/Mermaid en el modelo de datos. La exportación
local de pgAdmin y los scripts manuales originales siguen recuperables en el historial Git.
