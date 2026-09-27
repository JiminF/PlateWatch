package co.intecdl.platewatch.backend.local

import co.intecdl.platewatch.data.local.entity.AppSettingsEntity
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import co.intecdl.platewatch.data.local.entity.ObservedPlateEntity
import co.intecdl.platewatch.data.local.entity.WatchedPlateEntity
import kotlinx.coroutines.flow.Flow

interface LocalBackend {
    val status: Flow<LocalBackendStatus>
    fun watchedPlates(): Flow<List<WatchedPlateEntity>>
    fun observedPlates(): Flow<List<ObservedPlateEntity>>
    fun detections(): Flow<List<DetectionEntity>>
    fun settings(): Flow<AppSettingsEntity>
    suspend fun processConfirmedDetection(input: ConfirmedDetectionInput): DetectionProcessingResult
    suspend fun saveSettings(settings: AppSettingsEntity)
    suspend fun deleteDetection(id: Long)
    suspend fun deleteAllDetections()
    suspend fun rebuildObservedSummaries()
}
