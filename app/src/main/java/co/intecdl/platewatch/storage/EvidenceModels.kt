package co.intecdl.platewatch.storage
import android.graphics.Bitmap
data class EvidenceRequest(val plate:String,val confidence:Float,val detectedAtUtcMillis:Long,val location:co.intecdl.platewatch.location.DeviceLocation?,val frame:Bitmap)
data class EvidencePaths(val original:String?,val stamped:String?)
interface EvidenceRepository { suspend fun save(request:EvidenceRequest,saveOriginal:Boolean,saveStamped:Boolean,quality:Int):EvidencePaths; suspend fun delete(paths:EvidencePaths) }
