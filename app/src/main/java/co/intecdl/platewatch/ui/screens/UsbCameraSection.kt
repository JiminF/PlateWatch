package co.intecdl.platewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.camera.domain.UsbCameraDescriptor
import co.intecdl.platewatch.camera.domain.UsbCameraEvent
import co.intecdl.platewatch.camera.usb.UsbCameraCatalog
import co.intecdl.platewatch.camera.usb.UsbCameraMonitor

@Composable
fun UsbCameraSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val catalog = remember { UsbCameraCatalog(context) }
    val monitor = remember { UsbCameraMonitor(context) }
    var cameras by remember { mutableStateOf(catalog.listProbableUvcCameras()) }
    var status by remember { mutableStateOf("USB: esperando dispositivo UVC") }

    DisposableEffect(monitor) {
        monitor.start()
        onDispose { monitor.close() }
    }
    LaunchedEffect(monitor) {
        monitor.events.collect { event ->
            when (event.type) {
                UsbCameraEvent.Type.ATTACHED -> status = "USB CAMERA ATTACHED"
                UsbCameraEvent.Type.DETACHED -> status = "CAMERA DISCONNECTED"
                UsbCameraEvent.Type.PERMISSION_GRANTED -> status = "USB PERMISSION GRANTED"
                UsbCameraEvent.Type.PERMISSION_DENIED -> status = "USB PERMISSION DENIED"
                UsbCameraEvent.Type.ERROR -> status = event.message ?: "USB ERROR"
                UsbCameraEvent.Type.INVENTORY_CHANGED -> Unit
            }
            cameras = catalog.listProbableUvcCameras()
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Camaras USB", style = MaterialTheme.typography.titleLarge)
        Text(status)
        if (cameras.isEmpty()) Text("No se detectaron interfaces USB Video Class.")
        cameras.forEach { camera -> UsbCameraCard(camera) { monitor.requestPermission(camera.deviceId) } }
    }
}

@Composable
private fun UsbCameraCard(camera: UsbCameraDescriptor, requestPermission: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(camera.productName ?: "Camara USB UVC", style = MaterialTheme.typography.titleMedium)
            Text("VID: ${camera.vendorId} | PID: ${camera.productId} | ID: ${camera.deviceId}")
            Text("Control UVC: ${camera.hasVideoControlInterface} | Streaming UVC: ${camera.hasVideoStreamingInterface}")
            if (camera.permissionGranted) {
                Text("Permiso USB concedido", color = MaterialTheme.colorScheme.primary)
                Text("UVC BACKEND NOT AVAILABLE")
            } else {
                Button(onClick = requestPermission) { Text("SOLICITAR PERMISO USB") }
            }
        }
    }
}
