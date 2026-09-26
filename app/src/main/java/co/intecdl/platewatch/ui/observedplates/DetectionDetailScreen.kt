package co.intecdl.platewatch.ui.observedplates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import co.intecdl.platewatch.data.repository.ObservedPlateRepository

@Composable
fun DetectionDetailRoute(
    detectionId: Long,
    repository: ObservedPlateRepository,
    onBack: () -> Unit
) {
    val detection by repository.observeDetection(detectionId).collectAsState(initial = null)
    DetectionDetailScreen(detection = detection, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionDetailScreen(detection: DetectionEntity?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de deteccion") },
                navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } }
            )
        }
    ) { padding ->
        if (detection == null) {
            Box(Modifier.padding(padding).fillMaxSize().padding(20.dp)) {
                Text("Deteccion no disponible")
            }
        } else {
            Column(
                Modifier.padding(padding).fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(detection.normalizedPlate, style = MaterialTheme.typography.headlineLarge)
                DetailLine("Fecha y hora", formatDateTime(detection.detectedAtUtcMillis))
                DetailLine("Confianza", "${(detection.confidence * 100).toInt()}%")
                DetailLine("Estado", if (detection.matched) "COINCIDENCIA" else "NO COINCIDENTE")
                DetailLine("GPS", locationSummary(detection))
                DetailLine("Precision", detection.accuracyMeters?.let { "%.1f m".format(it) } ?: "No disponible")
                DetailLine("Camara", detection.cameraId ?: "No registrada")
                DetailLine("Imagen original", detection.originalImagePath ?: "No disponible")
                DetailLine("Imagen estampada", detection.stampedImagePath ?: "No disponible")
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
