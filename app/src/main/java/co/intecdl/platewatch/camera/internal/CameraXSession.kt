package co.intecdl.platewatch.camera.internal
import android.content.Context
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import co.intecdl.platewatch.camera.domain.AnalysisMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class CameraXSession(private val context:Context){
 private val executor=Executors.newSingleThreadExecutor(); private var provider:ProcessCameraProvider?=null
 private val received=AtomicLong(0); private var windowStart=System.nanoTime(); private var windowFrames=0L
 private val _metrics=MutableStateFlow(AnalysisMetrics()); val metrics:StateFlow<AnalysisMetrics> = _metrics
 fun bind(owner:LifecycleOwner,previewView:PreviewView,cameraId:String){
  _metrics.value=AnalysisMetrics(cameraState="CONNECTING")
  val future=ProcessCameraProvider.getInstance(context)
  future.addListener({ runCatching {
   val p=future.get(); provider=p; p.unbindAll()
   val selector=CameraSelector.Builder().addCameraFilter { infos -> infos.filter { Camera2CameraInfo.from(it).cameraId==cameraId } }.build()
   val preview=Preview.Builder().build().also { it.surfaceProvider=previewView.surfaceProvider }
   val analysis=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888).build()
   analysis.setAnalyzer(executor){ image -> analyze(image) }
   p.bindToLifecycle(owner,selector,preview,analysis); _metrics.value=_metrics.value.copy(cameraState="CONNECTED",lastError=null)
  }.onFailure { _metrics.value=_metrics.value.copy(cameraState="ERROR",lastError=it.message) } },ContextCompat.getMainExecutor(context))
 }
 private fun analyze(image:ImageProxy){
  val started=System.nanoTime(); val total=received.incrementAndGet(); windowFrames++
  val now=System.nanoTime(); val elapsed=(now-windowStart)/1_000_000_000.0
  val fps=if(elapsed>=1.0){ val value=windowFrames/elapsed; windowFrames=0; windowStart=now; value } else _metrics.value.analysisFps
  _metrics.value=AnalysisMetrics("CONNECTED",total,fps,(System.nanoTime()-started)/1_000_000,image.width,image.height,null)
  // El frame se cierra siempre. El motor ALPR real se conectara aqui mediante una interfaz, sin resultados simulados.
  image.close()
 }
 fun unbind(){ provider?.unbindAll(); _metrics.value=_metrics.value.copy(cameraState="DISCONNECTED") }
 fun close(){ unbind(); executor.shutdown() }
}
