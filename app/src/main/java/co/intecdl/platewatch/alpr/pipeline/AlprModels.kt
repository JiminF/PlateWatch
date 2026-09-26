package co.intecdl.platewatch.alpr.pipeline

import android.graphics.Bitmap
import android.graphics.RectF

enum class ModelState { NOT_AVAILABLE, LOADING, READY, ERROR }
enum class OperatingMode { RECOGNIZE, REGISTER, BOTH }
data class PlateRegion(val box: RectF, val confidence: Float, val crop: Bitmap)
data class OcrReading(val text: String, val confidence: Float)
data class AlprResult(val text: String, val detectorConfidence: Float, val ocrConfidence: Float, val box: RectF, val crop: Bitmap, val totalLatencyMs: Long)
data class PipelineMetrics(val preprocessMs: Long=0, val detectorMs: Long=0, val ocrMs: Long=0, val totalMs: Long=0)
sealed interface AlprOutcome { data class Success(val results: List<AlprResult>, val metrics: PipelineMetrics):AlprOutcome; data class Unavailable(val reason:String):AlprOutcome; data class Failure(val reason:String):AlprOutcome }
