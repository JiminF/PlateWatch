package co.intecdl.platewatch.ui.detections

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import co.intecdl.platewatch.ui.observedplates.formatDateTime
import co.intecdl.platewatch.ui.observedplates.locationSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionsScreen(
    state: DetectionsUiState,
    onQueryChange: (String) -> Unit,
    onMatchFilterChange: (MatchFilter) -> Unit,
    onDateFilterChange: (DateFilter) -> Unit,
    onOpenDetail: (Long) -> Unit,
    onRequestDelete: (DetectionEntity) -> Unit,
    onRequestDeleteAll: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDeleteAll: () -> Unit,
    onConfirmDeleteAll: () -> Unit,
    onMessageShown: () -> Unit,
    onBack: () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            onMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detecciones") },
                navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } },
                actions = { TextButton(onClick = onRequestDeleteAll, enabled = state.totalCount > 0) { Text("BORRAR TODO") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Total", state.totalCount.toString(), Modifier.weight(1f))
                SummaryCard("Coincidencias", state.matchedCount.toString(), Modifier.weight(1f))
            }
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar por placa") },
                singleLine = true
            )
            FilterRow(
                title = "Tipo",
                values = MatchFilter.entries,
                selected = state.matchFilter,
                label = { it.label },
                onSelected = onMatchFilterChange
            )
            FilterRow(
                title = "Fecha",
                values = DateFilter.entries,
                selected = state.dateFilter,
                label = { it.label },
                onSelected = onDateFilterChange
            )
            Text("${state.detections.size} resultado(s)")
            if (state.detections.isEmpty()) {
                Text("No hay detecciones que coincidan con los filtros.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.detections, key = { it.id }) { detection ->
                        DetectionCard(
                            detection = detection,
                            onOpen = { onOpenDetail(detection.id) },
                            onDelete = { onRequestDelete(detection) }
                        )
                    }
                }
            }
        }
    }

    state.deleteCandidate?.let { detection ->
        AlertDialog(
            onDismissRequest = onCancelDelete,
            title = { Text("Eliminar deteccion") },
            text = { Text("Se eliminara el evento ${detection.normalizedPlate} del ${formatDateTime(detection.detectedAtUtcMillis)}. Las fotografias se gestionaran en la etapa de evidencias.") },
            confirmButton = { Button(onClick = onConfirmDelete) { Text("ELIMINAR") } },
            dismissButton = { TextButton(onClick = onCancelDelete) { Text("CANCELAR") } }
        )
    }

    if (state.confirmDeleteAll) {
        AlertDialog(
            onDismissRequest = onCancelDeleteAll,
            title = { Text("Borrar historial") },
            text = { Text("Se eliminaran todas las detecciones. Esta accion no se puede deshacer. Las placas vigiladas no se borraran.") },
            confirmButton = { Button(onClick = onConfirmDeleteAll) { Text("BORRAR TODO") } },
            dismissButton = { TextButton(onClick = onCancelDeleteAll) { Text("CANCELAR") } }
        )
    }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun <T> FilterRow(
    title: String,
    values: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                FilterChip(
                    selected = value == selected,
                    onClick = { onSelected(value) },
                    label = { Text(label(value)) }
                )
            }
        }
    }
}

@Composable
private fun DetectionCard(
    detection: DetectionEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(detection.normalizedPlate, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    if (detection.matched) "COINCIDENCIA" else "OBSERVADA",
                    color = if (detection.matched) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            Text(formatDateTime(detection.detectedAtUtcMillis))
            Text("Confianza: ${(detection.confidence * 100).toInt()}%")
            Text(locationSummary(detection))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpen) { Text("VER DETALLE") }
                TextButton(onClick = onDelete) { Text("ELIMINAR") }
            }
        }
    }
}
