package co.intecdl.platewatch.camera.domain

import android.graphics.Bitmap
import android.util.Size

enum class CameraKind { INTERNAL, USB }
enum class CameraConnectionState {
DISCONNECTED,
CONNECTING,
CONNECTED,
ERROR
}

data class CameraDescriptor(    
val id:String,
val displayName:String,
val kind:CameraKind,
val isLogical:Boolean=false,
val physicalCameraIds:Set<String> = emptySet()
)


enum class CameraConnectionState {
DISCONNECTED,
CONNECTING,
CONNECTED,
ERROR
}

data class CameraCapabilities(
val resolutions: List<Size> = emptyList(),
val fpsRanges: List<IntRange> = emptyList()
)

data class CameraFrame(
val bitmap: Bitmap,
val timestampNanos: Long,
val rotationDegrees: Int
)

data class AnalysisMetrics(
val cameraState:String="DISCONNECTED",
val framesReceived:Long=0,
val analysisFps:Double=0.0,
val lastFrameLatencyMs:Long=0,
val width:Int=0,
val height:Int=0,
val lastError:String?=null
)

