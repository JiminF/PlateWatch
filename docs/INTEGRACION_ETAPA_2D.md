# Integracion Etapa 2D

## Copiar

Combinar `app/` con el proyecto actual y reemplazar `DetectionDao.kt`.

## Probar temporalmente

En `MainActivity.kt`:

```kotlin
import co.intecdl.platewatch.ui.detections.DetectionsRoute

setContent {
    PlateWatchTheme {
        DetectionsRoute(onExit = {})
    }
}
```

## Compilar

```powershell
.\gradlew.bat clean
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

## Pruebas manuales

1. Verificar estado vacio si no hay eventos.
2. Buscar una placa existente.
3. Alternar Todas, Coincidencias y No coincidentes.
4. Alternar Todo, Hoy, 7 dias y 30 dias.
5. Abrir detalle.
6. Eliminar un evento.
7. Confirmar que Borrar todo exige confirmacion.
8. Confirmar que las placas vigiladas no se eliminan.

## Nota de consistencia

Al borrar detecciones, el resumen de `observed_plates` no se recalcula aun. Esa operacion se incorporara antes de produccion para que el contador y las fechas permanezcan consistentes tras eliminaciones manuales.
