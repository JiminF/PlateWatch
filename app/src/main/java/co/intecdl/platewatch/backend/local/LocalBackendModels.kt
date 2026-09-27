package co.intecdl.platewatch.backend.local

import co.intecdl.platewatch.data.local.entity.OperatingMode

data class LocalBackendStatus(
    val databaseReady: Boolean,
    val storageReady: Boolean,
    val alprReady: Boolean,
    val message: String
)

data class ConfirmedDetectionInput(
    val rawPlate: String,
    val confidence: Float,
    val detectedAtUtcMillis: Long,
    val cameraId: String?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Float? = null,
    val locationTimestampUtcMillis: Long? = null,
    val originalImagePath: String? = null,
    val stampedImagePath: String? = null
)

data class DetectionProcessingResult(
    val detectionId: Long?,
    val normalizedPlate: String,
    val matched: Boolean,
    val registered: Boolean,
    val shouldAlert: Boolean,
    val mode: OperatingMode
)
