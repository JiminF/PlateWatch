package co.intecdl.platewatch.alpr.domain

import android.graphics.Bitmap
import android.graphics.RectF

data class PlateDetection(val plateText: String, val confidence: Float, val boundingBox: RectF, val plateImage: Bitmap)

interface PlateRecognitionEngine {
    suspend fun process(frame: Bitmap): List<PlateDetection>
}
