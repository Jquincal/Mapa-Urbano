# Mapa Urbano Android — frontend

Aplicación ciudadana nativa construida con Kotlin y Jetpack Compose. Esta carpeta contiene únicamente el frontend: usa datos de demostración en memoria y no consume todavía el backend Ktor, un SDK de mapas, cámara ni GPS reales.

## Alcance implementado

- Mapa ilustrado y vista de lista con el mismo conjunto de reportes.
- Búsqueda y filtros combinables por estado y categoría.
- Detalle público sin identidad del vecino.
- Formulario con modalidades cuenta y anónima, borrador, ubicación y foto simuladas.
- Confirmación con código opaco únicamente para reportes anónimos.
- Seguimiento por código con historial de estados.
- Inicio de sesión, registro, perfil y Mis reportes.
- Avisos para funciones futuras sin simular operaciones exitosas.
- Estados de carga, vacío, error y validación.
- Navegación inferior de cuatro destinos, retorno de autenticación y descarte/conservación de borrador.

## Datos de demostración

- Correo sugerido: `valeria@correo.com`
- Contraseña: cualquier valor de al menos 4 caracteres para iniciar sesión.
- Código de seguimiento existente: `7F2K-9B1M-4X3P`

Los datos viven en `core/data/DemoContent.kt`. `AppViewModel` mantiene una única fuente de estado para que los reportes creados aparezcan en confirmación, seguimiento y Mis reportes según su modalidad.

## Estructura

```text
app/src/main/java/com/mapaurbano/app/
├── MainActivity.kt
├── MapaUrbanoApp.kt
├── core/
│   ├── data/          # Estado y contenido ficticio
│   ├── designsystem/  # Tema, color, tipografía y tokens
│   ├── model/         # Modelos de presentación
│   └── navigation/    # Destinos tipados
└── feature/
    ├── auth/
    ├── common/
    ├── map/
    ├── myreports/
    ├── report/
    └── tracking/
```

El mapa, la cámara, la ubicación y la red están deliberadamente desacoplados. Podrán reemplazarse por adaptadores reales cuando el equipo apruebe proveedor de mapas y contratos del backend.

## Configuración

- `minSdk`: 26
- `targetSdk` / `compileSdk`: 37
- Android Gradle Plugin: 9.4.0
- Gradle: 9.6.0
- Compose BOM: 2026.08.00
- JDK: 17

## Verificación realizada

La entrega fue compilada con JDK 17 y validada mediante:

- pruebas unitarias de la variante `debug`;
- análisis estático de Android sin errores;
- generación correcta del APK `debug`;
- recorrido visual y funcional en un Redmi Note 13 Pro 5G físico.

La navegación inferior respeta las áreas seguras de Android y la barra del sistema se oculta automáticamente, con reaparición temporal mediante gesto. El emulador no forma parte de esta validación, por decisión del equipo.
