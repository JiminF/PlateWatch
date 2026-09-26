package co.intecdl.platewatch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = darkColorScheme(primary = Color(0xFF4DD6A8), secondary = Color(0xFF72A7FF), background = Color(0xFF07111F), surface = Color(0xFF0E1B2B))
@Composable fun PlateWatchTheme(content: @Composable () -> Unit) { MaterialTheme(colorScheme = Colors, content = content) }
