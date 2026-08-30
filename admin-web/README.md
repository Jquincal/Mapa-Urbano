# admin-web

Directorio reservado para el futuro panel web administrativo.

La base Angular de demostración fue retirada del repositorio para que el equipo inicie la implementación sobre contratos y decisiones aprobadas, sin heredar código de prototipo.

Cuando comience el desarrollo, el panel se creará con Angular y TypeScript en una rama `feat/*`, usando componentes standalone, Angular Router, `HttpClient`, Signals/RxJS y Leaflet.

Node.js/npm se usarán únicamente durante desarrollo y compilación. En producción, Ktor servirá los archivos estáticos bajo `/admin`; Node.js no será parte del runtime final.

Antes de generar el proyecto se deben cerrar:

- Versión de Angular y Node.js soportadas.
- Contrato inicial de autenticación y reportes.
- Estructura `core`, `shared` y `features`.
- Estrategia de testing y accesibilidad.
- Configuración de entornos sin secretos versionados.
