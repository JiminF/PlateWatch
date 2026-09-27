# Arquitectura local

```text
CameraX / UVC
      ↓
ALPR local
      ↓
DetectionStabilizer
      ↓
RoomLocalBackend
      ├── Room / SQLite privado
      ├── Lista vigilada local
      ├── Historial local
      ├── Configuracion local
      ├── Evidencias filesDir
      └── Notificacion Android local
```

`RoomLocalBackend` es el backend interno de la aplicacion. No abre puertos de red y no depende de un servidor separado.
