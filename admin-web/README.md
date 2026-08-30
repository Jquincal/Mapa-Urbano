# admin-web

Directorio reservado para el panel web administrativo.

El panel se implementará con Angular y TypeScript, usando componentes standalone, Angular Router, `HttpClient`, Signals/RxJS y Leaflet.

El proyecto tendrá `package.json` y utilizará Node.js/npm únicamente durante el desarrollo y la compilación. Node.js no será necesario como runtime de producción: Ktor servirá el contenido compilado bajo `/admin` desde el mismo despliegue del backend.

## Vista previa navegable

La primera vista completa del panel ya está implementada con datos simulados. Incluye dashboard, mapa, reportes, delegación, prioridades, estadísticas, configuración, auditoría y detalle lateral del reporte.

```bash
npm install
npm start
```

La aplicación queda disponible en `http://localhost:4200`. Para generar los archivos estáticos de producción:

```bash
npm run build
```
