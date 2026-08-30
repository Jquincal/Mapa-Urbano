# backend-ktor

Directorio reservado para el monolito modular Kotlin + Ktor.

La estructura de paquetes, los límites de los módulos y los contratos HTTP están definidos en [docs/01-arquitectura.md](../docs/01-arquitectura.md), [docs/02-modulos-backend.md](../docs/02-modulos-backend.md) y [docs/04-api-y-tiempo-real.md](../docs/04-api-y-tiempo-real.md).

En esta fase no se agrega código. El backend será el único proceso de aplicación y también servirá los archivos estáticos de `admin-web`.

El módulo `media` validará las imágenes y persistirá el contenido en `report_images.data BYTEA`; los clientes las obtendrán mediante endpoints binarios autorizados.
