package co.intecdl.platewatch.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import co.intecdl.platewatch.camera.internal.InternalCameraCatalog
import co.intecdl.platewatch.ui.screens.CameraSelectionScreen

@Composable
fun PlateWatchApp() {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    CameraSelectionScreen(granted, { launcher.launch(Manifest.permission.CAMERA) }, InternalCameraCatalog(context))
}
