package co.intecdl.platewatch.ui.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.backend.local.LocalBackend
import co.intecdl.platewatch.backend.local.LocalBackendStatus
import co.intecdl.platewatch.data.local.entity.AppSettingsEntity
import co.intecdl.platewatch.data.local.entity.OperatingMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(backend: LocalBackend, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val settings by backend.settings().collectAsState(initial = AppSettingsEntity())
    Scaffold(topBar = { TopAppBar(title = { Text("Configuración local") }, navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } }) }) { p ->
        Column(Modifier.padding(p).padding(18.dp).fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Modo de funcionamiento", style = MaterialTheme.typography.titleLarge)
            OperatingMode.entries.forEach { mode -> FilterChip(selected = settings.operatingMode == mode, onClick = { scope.launch { backend.saveSettings(settings.copy(operatingMode = mode)) } }, label = { Text(mode.name) }) }
            SettingSwitch("Sonido", settings.soundEnabled) { scope.launch { backend.saveSettings(settings.copy(soundEnabled = it)) } }
            SettingSwitch("Vibración", settings.vibrationEnabled) { scope.launch { backend.saveSettings(settings.copy(vibrationEnabled = it)) } }
            SettingSwitch("GPS", settings.gpsEnabled) { scope.launch { backend.saveSettings(settings.copy(gpsEnabled = it)) } }
            SettingSwitch("Guardar original", settings.saveOriginalImage) { scope.launch { backend.saveSettings(settings.copy(saveOriginalImage = it)) } }
            SettingSwitch("Guardar estampada", settings.saveStampedImage) { scope.launch { backend.saveSettings(settings.copy(saveStampedImage = it)) } }
            Text("Confianza mínima: ${(settings.minimumConfidence * 100).toInt()}%")
            Slider(value = settings.minimumConfidence, onValueChange = { scope.launch { backend.saveSettings(settings.copy(minimumConfidence = it)) } }, valueRange = .5f..1f)
        }
    }
}

@Composable private fun SettingSwitch(label: String, value: Boolean, change: (Boolean) -> Unit) { ElevatedCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Switch(checked = value, onCheckedChange = change) } } }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(backend: LocalBackend, onBack: () -> Unit) {
    val status by backend.status.collectAsState(initial = LocalBackendStatus(false, false, false, "Inicializando"))
    val watched by backend.watchedPlates().collectAsState(initial = emptyList())
    val observed by backend.observedPlates().collectAsState(initial = emptyList())
    val detections by backend.detections().collectAsState(initial = emptyList())
    Scaffold(topBar = { TopAppBar(title = { Text("Diagnóstico") }, navigationIcon = { TextButton(onClick = onBack) { Text("VOLVER") } }) }) { p ->
        Column(Modifier.padding(p).padding(18.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DiagnosticLine("Base local", if (status.databaseReady) "READY" else "ERROR")
            DiagnosticLine("Almacenamiento", if (status.storageReady) "READY" else "ERROR")
            DiagnosticLine("ALPR", if (status.alprReady) "READY" else "MODEL NOT AVAILABLE")
            DiagnosticLine("Placas vigiladas", watched.size.toString())
            DiagnosticLine("Placas observadas", observed.size.toString())
            DiagnosticLine("Detecciones", detections.size.toString())
            DiagnosticLine("Red requerida", "NO")
            Text(status.message, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
@Composable private fun DiagnosticLine(label: String, value: String) { ElevatedCard(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text(value, color = MaterialTheme.colorScheme.primary) } } }
