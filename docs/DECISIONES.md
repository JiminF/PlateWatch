# Decisiones tecnicas

- `minSdk 24`: base moderna para hardware USB y trabajo en segundo plano. Se revisara si un requisito obliga API 23.
- `CameraX`: preview y analisis; `Camera2`: inventario y metadatos avanzados.
- `CameraSource`: evita mezclar CameraX y UVC.
- Dependencias estables: las versiones de produccion se fijan y actualizan solo despues de compilar y probar.
