# Arquitectura de solución

## Decisión principal

Mapa Urbano será un monolito modular. Un único proceso Kotlin + Ktor contiene los módulos de negocio, expone la API REST, mantiene las conexiones WebSocket y sirve los archivos estáticos del panel web.

Android y el panel administrativo consumen el mismo backend. PostgreSQL + PostGIS es un servicio externo al proceso y almacena tanto los datos estructurados como las fotografías binarias.

## Vista de contexto

```mermaid
flowchart LR
    V[Vecino] --> A[Android<br/>Kotlin + Compose]
    M[Administrador municipal] --> W[Panel web<br/>Angular + TypeScript + Leaflet]
    A -->|HTTPS REST / WSS| K[Backend central<br/>Kotlin + Ktor]
    W -->|mismo origen<br/>HTTPS REST / WSS| K
    K --> P[(PostgreSQL + PostGIS<br/>datos e imágenes BYTEA)]
    K --> O[Logs y métricas]
```

## Capas del monolito

Cada módulo mantiene sus reglas de negocio y sus adaptadores. Las rutas HTTP no deben acceder directamente a tablas o clientes externos.

```text
Entrada HTTP/WebSocket
        ↓
API del módulo: serialización, autenticación y validación
        ↓
Aplicación: casos de uso y transacciones
        ↓
Dominio: entidades, estados y reglas
        ↓
Infraestructura: repositorios, PostGIS, almacenamiento binario y eventos
```

### Reglas de dependencia

- `api` puede depender de `application` y de contratos compartidos.
- `application` puede depender de `domain` y de puertos definidos por el módulo.
- `infrastructure` implementa puertos; no define reglas de negocio.
- `domain` no depende de Ktor, SQL ni clases de Android.
- `shared` contiene errores, reloj, identificadores, paginación y observabilidad transversal.
- Un módulo no consulta las tablas de otro módulo sin pasar por un contrato explícito.

## Módulos del backend

```text
backend-ktor/
└── src/main/kotlin/<paquete>/
    ├── application/          # Arranque de Ktor, configuración y routing raíz
    ├── shared/                # Tipos comunes, errores, paginación, métricas
    ├── auth/                  # Sesiones y autorización de administradores
    ├── reports/               # Alta, consulta, detalle y ciclo de vida
    ├── assignments/           # Equipos, responsables y delegación operativa
    ├── categories/            # Catálogo de categorías
    ├── media/                 # Validación y persistencia de fotografías
    ├── statistics/            # Agregaciones para el panel
    ├── notifications/         # Publicación y suscripción de eventos en tiempo real
    └── audit/                 # Registro inmutable de acciones relevantes
```

Las responsabilidades y contratos de cada módulo están en [02-modulos-backend.md](02-modulos-backend.md).

## Flujos principales

### Alta de reporte anónimo

```mermaid
sequenceDiagram
    actor Vecino
    participant Android
    participant Ktor
    participant DB as PostgreSQL/PostGIS
    participant WS as Suscriptores WebSocket

    Vecino->>Android: Selecciona punto y completa formulario
    Android->>Ktor: POST /api/v1/reports (multipart)
    Ktor->>Ktor: Valida campos, ubicación y fotografía
    Ktor->>Ktor: Genera código opaco y calcula hash
    Ktor->>Ktor: Valida y normaliza fotografía, si existe
    Ktor->>DB: Inserta reporte + report_image en una transacción
    DB-->>Ktor: Commit confirmado
    Ktor->>WS: Publica report.created sin datos sensibles
    Ktor-->>Android: 201 + id + trackingCode + estado inicial
    Android-->>Vecino: Muestra, copia y recomienda guardar el código
```

El reporte y su imagen se confirman en la misma transacción. Nunca se responde éxito si el binario, sus metadatos o el reporte no quedaron persistidos de forma coherente.

### Cambio de estado administrativo

```mermaid
sequenceDiagram
    actor Admin
    participant Panel
    participant Ktor
    participant DB as PostgreSQL/PostGIS
    participant WS as Android y panel

    Admin->>Panel: Selecciona nuevo estado
    Panel->>Ktor: PATCH /api/v1/admin/reports/{id}/status
    Ktor->>Ktor: Valida sesión y transición permitida
    Ktor->>DB: Actualiza report + status history + audit
    DB-->>Ktor: Commit confirmado
    Ktor->>WS: Publica report.updated y statistics.updated
    Ktor-->>Panel: Reporte actualizado
    WS-->>Panel: Refresca fila, mapa y métricas
    WS-->>Android: Refresca marcador y detalle
```

## Servir el panel Angular desde Ktor

`admin-web` será una aplicación Angular. El proceso de build generará archivos estáticos en `dist/`; Ktor los empaquetará dentro de la imagen o los montará como recursos de la aplicación.

| Ruta | Respuesta | Acceso |
|---|---|---|
| `/admin` | `index.html` del panel | Público solo para mostrar login |
| `/admin/assets/*` | CSS, JS, íconos y Leaflet | Público |
| `/api/v1/*` | API JSON o `multipart/form-data` | Según endpoint |
| `/api/v1/ws/*` | WebSocket | Según canal |
| `/health/live` | Estado de proceso | Interno |
| `/health/ready` | Estado de dependencias críticas | Interno |

Angular se desarrollará con Node.js/npm y su herramienta de build. Node.js no se ejecutará en producción: una etapa de compilación de Docker instalará dependencias y generará `dist/`, y la imagen final de Ktor contendrá solamente el backend y los assets compilados. Durante el desarrollo se podrá usar el servidor de Angular o servir el contenido compilado desde Ktor.

La configuración de producción debe usar un fallback de rutas para que `/admin` y sus rutas internas de Angular funcionen al recargar la página. Las llamadas a la API conservarán el mismo origen y el prefijo `/api/v1`.

## Despliegue lógico

```text
Internet
   │ HTTPS / WSS
   ▼
Proxy o balanceador TLS
   │
   ▼
Contenedor backend-ktor
   ├── API REST
   ├── WebSocket
   └── archivos estáticos admin-web
        │
        ├── PostgreSQL administrado + PostGIS
        │    └── datos e imágenes BYTEA
        └── servicio de logs/métricas
```

La terminación TLS puede estar en el proxy o en la plataforma de despliegue. El backend debe conocer `X-Forwarded-Proto` de un proxy confiable y rechazar tráfico inseguro en producción.

## Decisiones operativas iniciales

- Una imagen Docker reproducible para Ktor.
- Un proceso de aplicación por contenedor; el escalado horizontal se habilita cuando haya necesidad.
- PostgreSQL administrado con backups automáticos y prueba de restauración.
- Las fotografías se almacenan en `report_images.data BYTEA`, se entregan mediante endpoints autorizados y quedan incluidas en backups/restauraciones.
- Se monitorizan tamaño total, crecimiento, I/O y tiempo de restauración de la base, porque las imágenes aumentan su volumen.
- WebSocket con heartbeat, límite de conexiones y reconexión exponencial en los clientes.
- Migraciones versionadas ejecutadas antes de habilitar una nueva versión del backend.
- Configuración por variables de entorno o secreto administrado; ningún secreto en el repositorio.

## Límites intencionales

- El backend no contiene lógica de presentación de Compose.
- Android no accede a PostgreSQL directamente.
- El panel no ejecuta consultas SQL ni usa credenciales de infraestructura.
- Las fotografías no atraviesan WebSocket; el evento solo notifica que el reporte cambió.
- WebSocket no reemplaza REST: REST es la fuente de lectura inicial y WebSocket comunica cambios.
