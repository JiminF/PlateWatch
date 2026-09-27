# Integración UI integrada

## 1. Requisitos previos
Debe estar instalado el Bloque 09 Backend Local, las pantallas 2B, 2C y 2D, CameraX y la infraestructura USB.

## 2. Copiar
Combine la carpeta `app/` con el proyecto actual.

## 3. MainActivity
Reemplace la llamada principal por:

```kotlin
import co.intecdl.platewatch.ui.app.PlateWatchApp

setContent {
    PlateWatchTheme {
        PlateWatchApp()
    }
}
```

## 4. Dependencias
Mantenga `lifecycle-viewmodel-compose`, CameraX, Room, Coroutines y Material 3. Esta UI no necesita un paquete de iconos adicional.

## 5. Qué queda unido
- Dashboard consulta Room mediante LocalBackend.
- Configuración escribe AppSettings local.
- Selector presenta cámaras internas y USB en la misma pantalla.
- Cámara interna abre preview real CameraX.
- USB detecta, solicita permiso y no finge preview si AUSBC todavía no está validado.
- Listas vigiladas, observadas y detecciones reutilizan sus pantallas reales.

## 6. Compilar

```powershell
.\gradlew.bat clean
.\gradlew.bat test
.\gradlew.bat assembleDebug
```
