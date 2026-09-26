package co.intecdl.platewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.camera.domain.CameraCatalog
import co.intecdl.platewatch.camera.domain.CameraDescriptor

@Composable
fun CameraSelectionScreen(permissionGranted: Boolean, requestPermission: () -> Unit, catalog: CameraCatalog) {
    var cameras by remember { mutableStateOf<List<CameraDescriptor>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) runCatching { catalog.listCameras() }.onSuccess { cameras = it }.onFailure { error = it.message }
    }
    Scaffold(topBar = { TopAppBar(title = { Text("PLATE WATCH") }) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Seleccionar camara", style = MaterialTheme.typography.headlineMedium)
            if (!permissionGranted) {
                Text("Se necesita permiso de camara para consultar dispositivos reales.")
                Button(onClick = requestPermission) { Text("CONCEDER PERMISO") }
            } else if (error != null) {
                Text("NO CAMERA: ${error ?: "Error desconocido"}", color = MaterialTheme.colorScheme.error)
            } else if (cameras.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(cameras, key = { it.id }) { camera ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(camera.displayName, style = MaterialTheme.typography.titleMedium)
                                Text("ID: ${camera.id}")
                                Text(if (camera.isLogical) "Camara logica | Fisicas: ${camera.physicalCameraIds.joinToString().ifBlank { "no expuestas" }}" else "Camara fisica")
                            }
                        }
                    }
                }
            }
        }
    }
}
