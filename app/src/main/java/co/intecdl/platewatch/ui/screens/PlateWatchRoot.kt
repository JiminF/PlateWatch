package co.intecdl.platewatch.ui.screens
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import co.intecdl.platewatch.camera.domain.CameraDescriptor
import co.intecdl.platewatch.camera.internal.InternalCameraCatalog

@Composable fun PlateWatchRoot(){
 val context=LocalContext.current; var granted by remember{mutableStateOf(ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)}
 val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){granted=it}; var selected by remember{mutableStateOf<CameraDescriptor?>(null)}
 if(selected!=null) CameraPreviewScreen(selected!!){selected=null} else CameraListScreen(granted,{launcher.launch(Manifest.permission.CAMERA)}){selected=it}
}
@Composable private fun CameraListScreen(granted:Boolean,request:()->Unit,onSelect:(CameraDescriptor)->Unit){
 val context=LocalContext.current; var cameras by remember{mutableStateOf(emptyList<CameraDescriptor>())}; var error by remember{mutableStateOf<String?>(null)}
 LaunchedEffect(granted){if(granted)runCatching{InternalCameraCatalog(context).list()}.onSuccess{cameras=it}.onFailure{error=it.message}}
 Scaffold(topBar={TopAppBar(title={Text("PLATE WATCH")})}){p->Column(Modifier.padding(p).padding(20.dp).fillMaxSize(),verticalArrangement=Arrangement.spacedBy(16.dp)){Text("Seleccionar camara",style=MaterialTheme.typography.headlineMedium); when{!granted->{Text("Permiso de camara requerido");Button(onClick=request){Text("CONCEDER PERMISO")}};error!=null->Text("NO CAMERA: $error",color=MaterialTheme.colorScheme.error);else->LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(cameras,key={it.id}){c->ElevatedCard(onClick={onSelect(c)},modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text(c.displayName);Text(if(c.isLogical)"Logica | fisicas: ${c.physicalCameraIds.joinToString().ifBlank{"no expuestas"}}" else "Fisica")}}}}}}}
}
