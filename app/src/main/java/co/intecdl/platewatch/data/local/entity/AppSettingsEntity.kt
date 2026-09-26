package co.intecdl.platewatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OperatingMode { RECOGNIZE, REGISTER, BOTH }

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val singletonId: Int = 1,
    val operatingMode: OperatingMode = OperatingMode.BOTH,
    val selectedCameraId: String? = null,
    val requestedWidth: Int? = null,
    val requestedHeight: Int? = null,
    val requestedFps: Int? = null,
    val analysisFps: Int = 8,
    val minimumConfidence: Float = 0.85f,
    val requiredReadings: Int = 3,
    val temporalWindowMillis: Long = 2_000,
    val cooldownMillis: Long = 10_000,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val gpsEnabled: Boolean = true,
    val saveOriginalImage: Boolean = true,
    val saveStampedImage: Boolean = true,
    val jpegQuality: Int = 90
)
