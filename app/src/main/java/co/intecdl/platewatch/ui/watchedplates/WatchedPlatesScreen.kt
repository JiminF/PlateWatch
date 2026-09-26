package co.intecdl.platewatch.ui.watchedplates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.data.local.entity.WatchedPlateEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchedPlatesScreen(
    viewModel: WatchedPlatesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        val text = when (state.message) {
            WatchedPlateMessage.Saved -> "Placa guardada"
            WatchedPlateMessage.Deleted -> "Placa eliminada"
            WatchedPlateMessage.Duplicate -> "La placa ya existe"
            WatchedPlateMessage.InvalidPlate -> "Escribe una placa valida"
            WatchedPlateMessage.NotFound -> "La placa ya no existe"
            null -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placas vigiladas") },
                navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } },
                actions = { TextButton(onClick = viewModel::add) { Text("+ AGREGAR") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar placa, etiqueta o nota") },
                singleLine = true
            )
            Text("${state.plates.size} resultado(s)", style = MaterialTheme.typography.labelLarge)
            if (state.plates.isEmpty()) {
                Text("No hay placas vigiladas para mostrar.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.plates, key = { it.id }) { entity ->
                        WatchedPlateCard(
                            entity = entity,
                            onEdit = { viewModel.edit(entity) },
                            onDelete = { viewModel.requestDelete(entity) },
                            onActiveChange = { viewModel.setActive(entity, it) }
                        )
                    }
                }
            }
        }
    }

    state.editor?.let { value ->
        WatchedPlateEditorDialog(
            value = value,
            onChange = viewModel::updateEditor,
            onDismiss = viewModel::closeEditor,
            onSave = viewModel::save
        )
    }

    state.deleteCandidate?.let { entity ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            title = { Text("Eliminar placa") },
            text = { Text("Se eliminara ${entity.normalizedPlate} de la lista de vigilancia. El historial de detecciones se conserva.") },
            confirmButton = { Button(onClick = viewModel::confirmDelete) { Text("ELIMINAR") } },
            dismissButton = { TextButton(onClick = viewModel::cancelDelete) { Text("CANCELAR") } }
        )
    }
}

@Composable
private fun WatchedPlateCard(
    entity: WatchedPlateEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(entity.normalizedPlate, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Switch(checked = entity.active, onCheckedChange = onActiveChange)
            }
            entity.label?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
            entity.notes?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            Text(if (entity.active) "ACTIVA" else "INACTIVA", color = if (entity.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("EDITAR") }
                TextButton(onClick = onDelete) { Text("ELIMINAR") }
            }
        }
    }
}

@Composable
private fun WatchedPlateEditorDialog(
    value: WatchedPlateEditorState,
    onChange: (WatchedPlateEditorState) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (value.id == null) "Agregar placa" else "Editar placa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = value.plate,
                    onValueChange = { onChange(value.copy(plate = it)) },
                    label = { Text("Placa") },
                    supportingText = { Text("Se guardara en mayusculas y sin espacios ni guiones") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = value.label,
                    onValueChange = { onChange(value.copy(label = it)) },
                    label = { Text("Nombre o etiqueta") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = value.notes,
                    onValueChange = { onChange(value.copy(notes = it)) },
                    label = { Text("Notas") },
                    minLines = 2,
                    maxLines = 4
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Placa activa")
                    Switch(checked = value.active, onCheckedChange = { onChange(value.copy(active = it)) })
                }
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("GUARDAR") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR") } }
    )
}
