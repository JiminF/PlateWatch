package co.intecdl.platewatch.ui.observedplates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import co.intecdl.platewatch.data.repository.ObservedPlateRepository

@Composable
fun ObservedPlateHistoryRoute(
    observedPlateId: Long,
    repository: ObservedPlateRepository,
    onOpenDetection: (Long) -> Unit,
    onBack: () -> Unit
) {
    val plate by repository.observePlate(observedPlateId).collectAsState(initial = null)
    val history by repository.observeHistory(observedPlateId).collectAsState(initial = emptyList())
    ObservedPlateHistoryScreen(
        plate = plate?.normalizedPlate ?: "Placa no disponible",
        count = plate?.observationCount ?: history.size.toLong(),
        history = history,
        onOpenDetection = onOpenDetection,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservedPlateHistoryScreen(
    plate: String,
    count: Long,
    history: List<DetectionEntity>,
    onOpenDetection: (Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de $plate") },
                navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("$count deteccion(es)", style = MaterialTheme.typography.titleMedium)
            if (history.isEmpty()) {
                Text("No existen eventos individuales para esta placa.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(history, key = { it.id }) { detection ->
                        DetectionHistoryCard(detection) { onOpenDetection(detection.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetectionHistoryCard(detection: DetectionEntity, onOpen: () -> Unit) {
    ElevatedCard(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatDateTime(detection.detectedAtUtcMillis), fontWeight = FontWeight.Bold)
                Text(if (detection.matched) "COINCIDENCIA" else "OBSERVADA", color = if (detection.matched) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
            Text("Confianza: ${(detection.confidence * 100).toInt()}%")
            Text(locationSummary(detection))
            Text("VER DETALLE", color = MaterialTheme.colorScheme.primary)
        }
    }
}

internal fun locationSummary(detection: DetectionEntity): String =
    if (detection.latitude != null && detection.longitude != null) {
        "GPS: %.6f, %.6f".format(detection.latitude, detection.longitude)
    } else {
        "GPS no disponible"
    }
