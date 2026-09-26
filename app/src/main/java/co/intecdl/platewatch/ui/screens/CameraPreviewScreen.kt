package co.intecdl.platewatch.ui.screens
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.intecdl.platewatch.camera.domain.CameraDescriptor
import co.intecdl.platewatch.camera.internal.CameraXSession

@Composable fun CameraPreviewScreen(camera:CameraDescriptor,onBack:()->Unit){
 val context=LocalContext.current; val owner=LocalLifecycleOwner.current; val session=remember{CameraXSession(context.applicationContext)}; val metrics by session.metrics.collectAsState()
 DisposableEffect(Unit){onDispose{session.close()}}
 Scaffold(topBar={TopAppBar(title={Text(camera.displayName)},navigationIcon={TextButton(onClick=onBack){Text("VOLVER")}})}){p->Column(Modifier.padding(p).fillMaxSize()){AndroidView(factory={c->PreviewView(c).also{it.scaleType=PreviewView.ScaleType.FILL_CENTER;session.bind(owner,it,camera.id)}},modifier=Modifier.fillMaxWidth().weight(1f));Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(4.dp)){Text("Estado: ${metrics.cameraState}");Text("Resolucion: ${metrics.width} x ${metrics.height}");Text("Frames: ${metrics.framesReceived}");Text("Analysis FPS: ${"%.1f".format(metrics.analysisFps)}");Text("Latencia analyzer: ${metrics.lastFrameLatencyMs} ms");Text("ALPR MODEL NOT AVAILABLE",color=MaterialTheme.colorScheme.secondary);metrics.lastError?.let{Text("PROCESSING ERROR: $it",color=MaterialTheme.colorScheme.error)}}}}
}
