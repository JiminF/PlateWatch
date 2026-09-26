package co.intecdl.platewatch.camera.domain

enum class CameraKind { INTERNAL, USB }
data class CameraDescriptor(val id:String,val displayName:String,val kind:CameraKind,val isLogical:Boolean=false,val physicalCameraIds:Set<String> = emptySet())
data class AnalysisMetrics(val cameraState:String="DISCONNECTED",val framesReceived:Long=0,val analysisFps:Double=0.0,val lastFrameLatencyMs:Long=0,val width:Int=0,val height:Int=0,val lastError:String?=null)
