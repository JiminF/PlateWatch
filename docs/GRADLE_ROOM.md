# Integracion Gradle para Room

## 1. `gradle/libs.versions.toml`

Agregar en `[versions]`:

```toml
room = "2.8.5"
ksp = "2.1.20-2.0.1"
```

Agregar en `[libraries]`:

```toml
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
room-testing = { module = "androidx.room:room-testing", version.ref = "room" }
```

Agregar en `[plugins]`:

```toml
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

## 2. `build.gradle.kts` raiz

Agregar:

```kotlin
alias(libs.plugins.ksp) apply false
```

## 3. `app/build.gradle.kts`

En plugins:

```kotlin
alias(libs.plugins.ksp)
```

En dependencies:

```kotlin
implementation(libs.room.runtime)
implementation(libs.room.ktx)
ksp(libs.room.compiler)
testImplementation(libs.room.testing)
```

En `android { defaultConfig { } }`:

```kotlin
javaCompileOptions {
    annotationProcessorOptions {
        arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
    }
}
```

Si KSP informa incompatibilidad con Kotlin, no cambies versiones al azar. Copia el error completo para alinear ambas versiones.
