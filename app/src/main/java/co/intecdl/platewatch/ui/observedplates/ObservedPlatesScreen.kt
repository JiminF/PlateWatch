package co.intecdl.platewatch.ui.observedplates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.data.local.entity.ObservedPlateEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservedPlatesScreen(
    state: ObservedPlatesUiState,
    onQueryChange: (String) -> Unit,
    onOpenHistory: (Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placas observadas") },
                navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar placa") },
                singleLine = true
            )
            Text("${state.plates.size} placa(s) observada(s)")
            if (state.plates.isEmpty()) {
                Text("Todavia no hay placas registradas en modo Registrar o Ambos.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.plates, key = { it.id }) { plate ->
                        ObservedPlateCard(plate) { onOpenHistory(plate.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObservedPlateCard(entity: ObservedPlateEntity, onOpen: () -> Unit) {
    ElevatedCard(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(entity.normalizedPlate, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${entity.observationCount} deteccion(es)")
            Text("Primera: ${formatDateTime(entity.firstSeenAtUtcMillis)}")
            Text("Ultima: ${formatDateTime(entity.lastSeenAtUtcMillis)}")
            Text("VER HISTORIAL", color = MaterialTheme.colorScheme.primary)
        }
    }
}

internal fun formatDateTime(utcMillis: Long): String =
    Instant.ofEpochMilli(utcMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
