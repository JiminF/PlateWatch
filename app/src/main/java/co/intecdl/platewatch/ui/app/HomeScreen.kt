package co.intecdl.platewatch.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.intecdl.platewatch.backend.local.LocalBackend
import co.intecdl.platewatch.backend.local.LocalBackendStatus

@Composable
fun HomeRoute(
    backend: LocalBackend,
    onCameras: () -> Unit,
    onWatched: () -> Unit,
    onObserved: () -> Unit,
    onDetections: () -> Unit,
    onSettings: () -> Unit,
    onDiagnostics: () -> Unit
) {
    val status by backend.status.collectAsState(initial = LocalBackendStatus(false, false, false, "Inicializando"))
    val watched by backend.watchedPlates().collectAsState(initial = emptyList())
    val observed by backend.observedPlates().collectAsState(initial = emptyList())
    val detections by backend.detections().collectAsState(initial = emptyList())
    val matches = remember(detections) { detections.count { it.matched } }

    HomeScreen(
        status = status,
        watched = watched.count { it.active },
        observed = observed.size,
        detections = detections.size,
        matches = matches,
        onCameras = onCameras,
        onWatched = onWatched,
        onObserved = onObserved,
        onDetections = onDetections,
        onSettings = onSettings,
        onDiagnostics = onDiagnostics
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    status: LocalBackendStatus,
    watched: Int,
    observed: Int,
    detections: Int,
    matches: Int,
    onCameras: () -> Unit,
    onWatched: () -> Unit,
    onObserved: () -> Unit,
    onDetections: () -> Unit,
    onSettings: () -> Unit,
    onDiagnostics: () -> Unit
) {
    val gradient = Brush.linearGradient(listOf(Color(0xFF071426), Color(0xFF102B45), Color(0xFF075B52)))
    Scaffold(containerColor = Color.Transparent) { padding ->
        Column(
            Modifier.fillMaxSize().background(gradient).padding(padding).verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("PLATE WATCH", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Centro de vigilancia local", color = Color(0xFFB9D9E8))
                }
                StatusDot(status.databaseReady && status.storageReady)
            }
            HeroCard(status, onCameras)
            Text("Resumen local", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Vigiladas", watched.toString(), Color(0xFF41D6A3), Modifier.weight(1f))
                MetricCard("Observadas", observed.toString(), Color(0xFF65A8FF), Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Eventos", detections.toString(), Color(0xFFFFC857), Modifier.weight(1f))
                MetricCard("Alertas", matches.toString(), Color(0xFFFF7272), Modifier.weight(1f))
            }
            Text("Herramientas", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            MenuCard("Placas vigiladas", "Agrega, edita y activa vehículos", "WATCHLIST", onWatched)
            MenuCard("Placas observadas", "Consulta cantidades e historial", "HISTORIAL", onObserved)
            MenuCard("Detecciones", "Filtra eventos y coincidencias", "EVENTOS", onDetections)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactAction("Configuración", onSettings, Modifier.weight(1f))
                CompactAction("Diagnóstico", onDiagnostics, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable private fun StatusDot(active: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(12.dp).clip(CircleShape).background(if (active) Color(0xFF42E2A7) else Color(0xFFFF6B6B)))
        Text(if (active) "LOCAL" else "REVISAR", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable private fun HeroCard(status: LocalBackendStatus, onCameras: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .10f)), shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Vigilancia desde el dispositivo", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(status.message, color = Color(0xFFC6E6F2))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(if (status.databaseReady) "Base local lista" else "Base pendiente") })
                AssistChip(onClick = {}, label = { Text(if (status.alprReady) "ALPR activo" else "ALPR pendiente") })
            }
            Button(onClick = onCameras, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp)) {
                Text("SELECCIONAR CÁMARA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable private fun MetricCard(title: String, value: String, accent: Color, modifier: Modifier) {
    ElevatedCard(modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF11263A))) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineLarge, color = accent, fontWeight = FontWeight.Black)
            Text(title, color = Color(0xFFCCE0EA))
        }
    }
}

@Composable private fun MenuCard(title: String, subtitle: String, badge: String, onClick: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(22.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF11263A))) {
        Row(Modifier.padding(18.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(subtitle, color = Color(0xFFAFC6D3)) }
            Surface(color = Color(0xFF23455B), shape = RoundedCornerShape(50)) { Text(badge, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Color(0xFF63D8B4), style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@Composable private fun CompactAction(title: String, onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(52.dp), shape = RoundedCornerShape(16.dp)) { Text(title) }
}
