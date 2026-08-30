# Checklist de inicio

## Antes de escribir código

- [ ] Aprobar el alcance del MVP y la tabla de sustituciones.
- [ ] Confirmar que la raíz del repositorio será `Mapa Urbano` y no `/home/joaquin`.
- [ ] Definir ciudad, área operativa y sistema de coordenadas de trabajo.
- [ ] Elegir proveedor de PostgreSQL administrado con PostGIS.
- [ ] Definir capacidad de PostgreSQL, límite de imágenes y política de privacidad/retención.
- [ ] Elegir SDK de mapas para Android y proveedor de tiles.
- [ ] Confirmar estados, categorías y textos visibles.
- [ ] Confirmar equipos iniciales, responsables, permisos y reglas de reasignación.
- [ ] Confirmar valores de prioridad y criterio municipal para `urgent` y fecha objetivo.
- [ ] Definir límites de descripción, retención y política de baja.
- [ ] Validar mockups con stakeholders y registrar cambios.

## Orden recomendado de trabajo

1. Congelar contratos de datos y API.
2. Crear el repositorio dentro de la carpeta del proyecto.
3. Configurar Ktor, Docker, health checks y configuración segura.
4. Crear migraciones PostgreSQL/PostGIS y seeds.
5. Implementar el flujo de alta y consulta pública.
6. Integrar `report_images` y el endpoint binario.
7. Implementar autenticación y API administrativa.
8. Implementar asignación, prioridad e historial administrativo.
9. Construir el panel Angular y servir sus assets compilados desde Ktor.
10. Construir Android con el mismo contrato REST.
11. Agregar WebSocket, reconexión y sincronización.
12. Ejecutar pruebas de seguridad, rendimiento y accesibilidad.
13. Preparar staging, backups, restauración y manual operativo.

## Definition of Done de la primera entrega técnica

- [ ] El backend arranca dentro de Docker.
- [ ] El panel Angular se sirve desde Ktor sin Node como runtime ni servidor web adicional.
- [ ] PostgreSQL tiene PostGIS, tablas obligatorias, índices y categorías iniciales.
- [ ] Un reporte puede crearse con ubicación y estado `pending`.
- [ ] El código de seguimiento no expone el ID ni datos personales.
- [ ] Una fotografía válida termina en `report_images.data` con MIME, tamaño, checksum y dimensiones coherentes.
- [ ] El administrador puede iniciar sesión y cambiar el estado.
- [ ] El administrador puede asignar, reasignar y priorizar un reporte sin perder historial.
- [ ] Cada cambio genera historial y auditoría.
- [ ] Android puede listar, crear y consultar un reporte.
- [ ] El mapa y las estadísticas reaccionan a eventos WebSocket.
- [ ] Los errores conservan los datos recuperables del formulario.
- [ ] Las pruebas cubren rate limit, permisos, uploads y CSRF.
- [ ] Existe backup restaurado en un entorno de prueba.

## Entregables de la próxima iteración

1. Contrato API aprobado, incluyendo ejemplos y códigos de error.
2. Migración inicial revisada por backend y base de datos.
3. Decisión documentada del SDK de mapas Android.
4. Wireframes revisados con resultados de pruebas rápidas.
5. Backlog técnico priorizado para el Hito H1.
