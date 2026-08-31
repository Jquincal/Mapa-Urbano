# Backend Ktor

Base ejecutable del monolito modular de Mapa Urbano. En esta etapa están creados el proyecto Gradle, los módulos y el registro de rutas; todavía no se implementaron reglas de negocio, autenticación ni persistencia.

## Tecnologías

- JDK 21.
- Kotlin 2.4.10.
- Ktor 3.5.1 con Netty.
- Kotlinx Serialization para JSON.
- Gradle Wrapper 9.5.0.

## Ejecutar

Desde este directorio:

```bash
./gradlew test
./gradlew run
```

El servidor escucha por defecto en `http://localhost:8080`. Puede cambiarse el puerto mediante la variable de entorno `PORT`.

## Estado actual de la API

- `GET /health/live` responde `200`: el proceso está activo.
- `GET /health/ready` responde `503` hasta que la base de datos sea configurada.
- Las rutas HTTP de negocio están registradas y responden `501 NOT_IMPLEMENTED` con el formato de error común.
- Los dos canales WebSocket están registrados, pero cierran indicando que aún no están disponibles.

Una respuesta `501` es intencional: permite validar rutas e integración sin simular que una función incompleta ya está operativa.

## Organización

Cada módulo mantiene su entrada HTTP dentro de un paquete `api`. Al implementar la lógica se agregarán, según corresponda, las capas `application`, `domain` e `infrastructure` dentro del mismo módulo.

```text
src/main/kotlin/com/mapaurbano/
├── application/       # Arranque, plugins y registro central de rutas
├── auth/api/           # Sesiones administrativas
├── reports/api/        # Reportes públicos y administrativos
├── categories/api/     # Categorías públicas y administrativas
├── media/api/          # Entrega y futura carga de imágenes
├── assignments/api/    # Delegación, equipos y responsables
├── statistics/api/     # Métricas del panel
├── audit/api/          # Historial de acciones administrativas
├── notifications/api/  # WebSocket público y administrativo
├── health/api/         # Liveness y readiness
└── shared/api/         # Contratos HTTP compartidos
```

Los contratos funcionales están documentados en [arquitectura](../docs/01-arquitectura.md), [módulos backend](../docs/02-modulos-backend.md) y [API y tiempo real](../docs/04-api-y-tiempo-real.md).
