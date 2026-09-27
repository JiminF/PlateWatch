package co.intecdl.platewatch.ui.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.camera.domain.CameraDescriptor
import co.intecdl.platewatch.camera.domain.UsbCameraDescriptor
import co.intecdl.platewatch.camera.internal.InternalCameraCatalog
import co.intecdl.platewatch.camera.usb.UsbCameraCatalog
import co.intecdl.platewatch.camera.usb.UsbCameraMonitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedCameraScreen(
    permissionGranted: Boolean,
    requestPermission: () -> Unit,
    onInternalSelected: (CameraDescriptor) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val usbCatalog = remember { UsbCameraCatalog(context) }
    val usbMonitor = remember { UsbCameraMonitor(context) }
    var internal by remember { mutableStateOf(emptyList<CameraDescriptor>()) }
    var usb by remember { mutableStateOf(usbCatalog.listProbableUvcCameras()) }
    var message by remember { mutableStateOf<String?>(null) }

    DisposableEffect(usbMonitor) { usbMonitor.start(); onDispose { usbMonitor.close() } }
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) runCatching { InternalCameraCatalog(context).list() }.onSuccess { internal = it }.onFailure { message = it.message }
    }
    LaunchedEffect(usbMonitor) { usbMonitor.events.collect { usb = usbCatalog.listProbableUvcCameras(); message = it.type.name } }

    Scaffold(topBar = { TopAppBar(title = { Text("Cámaras disponibles") }, navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Elige una fuente", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("La cámara seleccionada alimentará el mismo pipeline local.")
                message?.let { Text(it, color = MaterialTheme.colorScheme.secondary) }
            }
            item { SectionTitle("Cámaras internas", "CameraX / Camera2") }
            if (!permissionGranted) item { Button(onClick = requestPermission, modifier = Modifier.fillMaxWidth()) { Text("CONCEDER PERMISO DE CÁMARA") } }
            else if (internal.isEmpty()) item { Text("No se encontraron cámaras internas.") }
            else items(internal, key = { it.id }) { camera -> InternalCameraCard(camera) { onInternalSelected(camera) } }
            item { HorizontalDivider(); SectionTitle("Cámaras USB", "USB Host / UVC") }
            if (usb.isEmpty()) item { Text("Conecta una cámara UVC mediante OTG. La lista se actualizará automáticamente.") }
            else items(usb, key = { it.stableId }) { camera -> UsbCameraCard(camera) { usbMonitor.requestPermission(camera.deviceId) } }
        }
    }
}

@Composable private fun SectionTitle(title: String, subtitle: String) { Column { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(subtitle, color = MaterialTheme.colorScheme.secondary) } }

@Composable private fun InternalCameraCard(camera: CameraDescriptor, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text(camera.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("ID: ${camera.id}"); Text(if (camera.isLogical) "Cámara lógica | físicas: ${camera.physicalCameraIds.joinToString().ifBlank { "no expuestas" }}" else "Cámara física"); Text("TOCAR PARA ABRIR PREVIEW", color = MaterialTheme.colorScheme.primary) } }
}

@Composable private fun UsbCameraCard(camera: UsbCameraDescriptor, request: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text(camera.productName ?: "Cámara USB UVC", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("VID ${camera.vendorId} | PID ${camera.productId}"); Text("Streaming: ${camera.hasVideoStreamingInterface}"); if (camera.permissionGranted) { Text("PERMISO CONCEDIDO", color = MaterialTheme.colorScheme.primary); Text("El preview requiere que el backend AUSBC esté validado.") } else Button(onClick = request) { Text("SOLICITAR PERMISO USB") } } }
}
