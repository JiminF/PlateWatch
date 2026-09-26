# Integracion Etapa 2C

## 1. Copiar

Combinar la carpeta `app/` con el proyecto actual. Reemplazar `ObservedPlateDao.kt` y `DetectionDao.kt` por estas versiones ampliadas.

## 2. Validacion temporal

En `MainActivity.kt`, para probar esta pantalla directamente:

```kotlin
import co.intecdl.platewatch.ui.observedplates.ObservedPlatesRoute

setContent {
    PlateWatchTheme {
        ObservedPlatesRoute(onExit = {})
    }
}
```

Despues de probar, restaurar la raiz habitual hasta construir el dashboard definitivo.

## 3. Importante

La pantalla no crea detecciones. Solo muestra registros reales existentes. Hasta integrar el motor ALPR, la lista puede aparecer vacia. Para una prueba controlada, se pueden insertar eventos desde Database Inspector, pero no se incluye informacion simulada en produccion.

## 4. Pruebas

1. Abrir la lista y comprobar el estado vacio.
2. Con registros reales, verificar contador, primera y ultima fecha.
3. Buscar por placa.
4. Abrir historial.
5. Abrir detalle.
6. Validar GPS disponible y no disponible.
7. Confirmar conversion de UTC a hora local del telefono.
