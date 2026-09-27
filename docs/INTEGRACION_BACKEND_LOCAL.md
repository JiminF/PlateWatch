# Integracion del backend completamente local

## 1. Copiar
Combine la carpeta `app/` con el proyecto.

## 2. Ampliar PlateWatchDatabase
En `PlateWatchDatabase.kt` agregue el import del DAO y este metodo abstracto:

```kotlin
abstract fun localBackendDao(): LocalBackendDao
```

No cambia el esquema de tablas, por lo cual la version puede permanecer en 1.

## 3. Registrar Application
En `app/src/main/AndroidManifest.xml`, cambie `<application ...>` para incluir:

```xml
android:name=".PlateWatchApplication"
```

Ejemplo:

```xml
<application
    android:name=".PlateWatchApplication"
    android:allowBackup="false"
    android:label="Plate Watch"
    android:theme="@style/Theme.PlateWatch"
    android:usesCleartextTraffic="false">
```

## 4. Acceso desde Activity o Compose

```kotlin
val backend = (application as PlateWatchApplication).container.localBackend
```

En Compose:

```kotlin
val app = LocalContext.current.applicationContext as PlateWatchApplication
val backend = app.container.localBackend
```

## 5. Almacenamiento local
- Base SQLite: almacenamiento interno privado de la app, archivo `platewatch.db`.
- Evidencias: `filesDir/PlateWatch/detections/...`.
- Sin servidor HTTP.
- Sin Firebase.
- Sin API remota.
- Sin Internet para consultar coincidencias.

## 6. Compilar

```powershell
.\gradlew.bat clean
.\gradlew.bat test
.\gradlew.bat assembleDebug
```
