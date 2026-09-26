package co.intecdl.platewatch.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detections",
    indices = [
        Index(value = ["normalizedPlate"]),
        Index(value = ["detectedAtUtcMillis"]),
        Index(value = ["matched"]),
        Index(value = ["observedPlateId"])
    ]
)
data class DetectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val observedPlateId: Long? = null,
    val normalizedPlate: String,
    val rawOcrText: String? = null,
    val confidence: Float,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Float? = null,
    val locationTimestampUtcMillis: Long? = null,
    val detectedAtUtcMillis: Long,
    val originalImagePath: String? = null,
    val stampedImagePath: String? = null,
    val matched: Boolean,
    val synced: Boolean = false,
    val cameraId: String? = null
)
