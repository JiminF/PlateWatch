package co.intecdl.platewatch.ui.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun DashboardScreen(onWatched:()->Unit,onObserved:()->Unit,onDetections:()->Unit){ Scaffold(topBar={TopAppBar(title={Text("PLATE WATCH")})}){p->Column(Modifier.padding(p).padding(20.dp).fillMaxSize(),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("Sistema",style=MaterialTheme.typography.headlineMedium);Text("ALPR MODEL NOT AVAILABLE");Button(onClick=onWatched,modifier=Modifier.fillMaxWidth()){Text("PLACAS VIGILADAS")};Button(onClick=onObserved,modifier=Modifier.fillMaxWidth()){Text("PLACAS OBSERVADAS")};Button(onClick=onDetections,modifier=Modifier.fillMaxWidth()){Text("DETECCIONES")};Text("Camara, configuracion y diagnostico se habilitan al validar sus backends reales.")}} }
