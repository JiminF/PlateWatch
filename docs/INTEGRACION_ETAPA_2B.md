# Integracion Etapa 2B

## 1. Copiar archivos

Combinar `app/` con la carpeta `app/` del proyecto actual.

## 2. Dependencia necesaria

En `gradle/libs.versions.toml`, verificar que exista:

```toml
lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycle" }
```

En `app/build.gradle.kts`:

```kotlin
implementation(libs.lifecycle.viewmodel.compose)
```

## 3. Abrir la pantalla temporalmente

Para validar la etapa sin rehacer todavia toda la navegacion, sustituir temporalmente el contenido de `setContent` en `MainActivity` por:

```kotlin
setContent {
    PlateWatchTheme {
        WatchedPlatesRoute(onBack = {})
    }
}
```

Importar:

```kotlin
import co.intecdl.platewatch.ui.watchedplates.WatchedPlatesRoute
```

Despues de comprobar CRUD, restaurar `PlateWatchRoot()` o integrar una ruta formal desde el dashboard.

## 4. Pruebas manuales

1. Agregar `ABC-123`.
2. Confirmar que se guarda como `ABC123`.
3. Intentar guardar `abc 123` y comprobar el mensaje de duplicado.
4. Editar etiqueta y notas.
5. Desactivar y reactivar.
6. Buscar por placa, etiqueta y nota.
7. Eliminar y confirmar que desaparece.
8. Cerrar y abrir la aplicacion para verificar persistencia.
