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
- [ ] Definir requisitos de contraseña, verificación de correo y política de baja/anonimización de vecinos.
- [ ] Validar mockups con stakeholders y registrar cambios.

## Orden recomendado de trabajo

1. Congelar contratos de datos y API.
2. Crear el repositorio dentro de la carpeta del proyecto.
3. Configurar Ktor, Docker, health checks y configuración segura.
4. Crear migraciones PostgreSQL/PostGIS y seeds.
5. Implementar registro, sesión revocable de vecinos y `Mis reportes`.
6. Implementar los modos de alta `account` y `anonymous`.
7. Integrar `report_images` y el endpoint binario.
8. Implementar autenticación y API administrativa.
9. Implementar asignación, prioridad e historial administrativo.
10. Construir el panel Angular y servir sus assets compilados desde Ktor.
11. Construir Android con el mismo contrato REST.
12. Agregar WebSocket, reconexión y sincronización.
13. Ejecutar pruebas de seguridad, rendimiento y accesibilidad.
14. Preparar staging, backups, restauración y manual operativo.

## Definition of Done de la primera entrega técnica

- [ ] El backend arranca dentro de Docker.
- [ ] El panel Angular se sirve desde Ktor sin Node como runtime ni servidor web adicional.
- [ ] PostgreSQL tiene PostGIS, tablas obligatorias, índices y categorías iniciales.
- [ ] Un vecino puede registrarse, iniciar/cerrar sesión y consultar solo sus reportes.
- [ ] Un reporte en modo cuenta deriva `user_id` de la sesión y no genera código.
- [ ] Un reporte anónimo guarda `user_id = NULL` y genera código de seguimiento.
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

## Aceptación de la interfaz de Stitch y futura implementación Android

- [x] Referencia visual y decisiones de alcance documentadas, sin ampliar el MVP.
- [x] Matriz de acciones, destinos, retornos, datos conservados y API definida.
- [x] Originales identificados y capturas versionadas; resultados separados de afirmaciones del generador.
- [ ] Resolver todas las limitaciones pendientes del [informe](13-verificacion-stitch.md) antes de declarar el prototipo conectado.
- [ ] Verificar todas las rutas y retornos en Android, incluidos login, registro, perfil, detalle y selector de ubicación.
- [ ] Probar borrador conservado/descartado, sesión vencida y cuenta/anónimo sin asociación accidental.
- [ ] Probar scroll con teclado real, altura reducida, 360/390 dp, texto 200% y movimiento reducido.
- [ ] Confirmar que los últimos campos, acciones, FAB y cierres de diálogos son alcanzables.
- [ ] Validar un archivo, reemplazo, 5 MB, tipo/firma inválida y permisos denegados en dispositivo y servidor.
- [ ] Verificar código opaco solo anónimo, mismo reporte en detalle/confirmación y copia/compartir sin éxito falso.
- [ ] Cerrar contrato de idempotencia antes de cualquier reenvío automático tras resultado incierto.
- [ ] Sustituir ejemplos municipales por contenido/contacto/área aprobados antes de producción.
- [ ] Completar pruebas REST/WS y fallos reales; las simulaciones de Stitch no acreditan esos casos.
