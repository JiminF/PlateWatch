# Integracion en PlateWatchRoot.kt

Importar:

```kotlin
import co.intecdl.platewatch.ui.screens.UsbCameraSection
```

Dentro del `Column` de la pantalla de seleccion, despues de la lista interna, agregar:

```kotlin
HorizontalDivider()
UsbCameraSection(modifier = Modifier.fillMaxWidth())
```

Si la lista interna consume todo el alto con `LazyColumn`, conviene convertir toda la pantalla en una sola `LazyColumn` y alojar la seccion USB como un `item`.
