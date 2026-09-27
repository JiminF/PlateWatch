package co.intecdl.platewatch.ui.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import co.intecdl.platewatch.PlateWatchApplication
import co.intecdl.platewatch.camera.domain.CameraDescriptor
import co.intecdl.platewatch.ui.detections.DetectionsRoute
import co.intecdl.platewatch.ui.observedplates.ObservedPlatesRoute
import co.intecdl.platewatch.ui.screens.CameraPreviewScreen
import co.intecdl.platewatch.ui.watchedplates.WatchedPlatesRoute

private enum class AppScreen { HOME, CAMERAS, WATCHED, OBSERVED, DETECTIONS, SETTINGS, DIAGNOSTICS }

@Composable
fun PlateWatchApp() {
    val context = LocalContext.current
    val application = context.applicationContext as PlateWatchApplication
    val backend = application.container.localBackend
    var screen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var selectedInternalCamera by remember { mutableStateOf<CameraDescriptor?>(null) }
    var cameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        cameraPermission = it
    }

    if (selectedInternalCamera != null) {
        BackHandler { selectedInternalCamera = null }
        CameraPreviewScreen(camera = selectedInternalCamera!!, onBack = { selectedInternalCamera = null })
        return
    }

    BackHandler(enabled = screen != AppScreen.HOME) { screen = AppScreen.HOME }

    when (screen) {
        AppScreen.HOME -> HomeRoute(
            backend = backend,
            onCameras = { screen = AppScreen.CAMERAS },
            onWatched = { screen = AppScreen.WATCHED },
            onObserved = { screen = AppScreen.OBSERVED },
            onDetections = { screen = AppScreen.DETECTIONS },
            onSettings = { screen = AppScreen.SETTINGS },
            onDiagnostics = { screen = AppScreen.DIAGNOSTICS }
        )
        AppScreen.CAMERAS -> UnifiedCameraScreen(
            permissionGranted = cameraPermission,
            requestPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            onInternalSelected = { selectedInternalCamera = it },
            onBack = { screen = AppScreen.HOME }
        )
        AppScreen.WATCHED -> WatchedPlatesRoute { screen = AppScreen.HOME }
        AppScreen.OBSERVED -> ObservedPlatesRoute { screen = AppScreen.HOME }
        AppScreen.DETECTIONS -> DetectionsRoute { screen = AppScreen.HOME }
        AppScreen.SETTINGS -> SettingsScreen(backend = backend, onBack = { screen = AppScreen.HOME })
        AppScreen.DIAGNOSTICS -> DiagnosticsScreen(backend = backend, onBack = { screen = AppScreen.HOME })
    }
}
