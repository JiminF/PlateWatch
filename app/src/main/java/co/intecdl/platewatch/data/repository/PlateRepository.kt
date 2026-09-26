package co.intecdl.platewatch.data.repository

import androidx.room.withTransaction
import co.intecdl.platewatch.alpr.core.PlateNormalizer
import co.intecdl.platewatch.data.local.database.PlateWatchDatabase
import co.intecdl.platewatch.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class PlateRepository(
    private val database: PlateWatchDatabase,
    private val normalizer: PlateNormalizer = PlateNormalizer()
) {
    fun watchedPlates(): Flow<List<WatchedPlateEntity>> = database.watchedPlateDao().observeAll()
    fun observedPlates(): Flow<List<ObservedPlateEntity>> = database.observedPlateDao().observeAll()
    fun detections(): Flow<List<DetectionEntity>> = database.detectionDao().observeAll()

    suspend fun addWatchedPlate(rawPlate: String, label: String?, notes: String?): Long {
        val plate = normalizer.normalize(rawPlate)
        require(plate.isNotBlank()) { "La placa normalizada no puede estar vacia" }
        val now = System.currentTimeMillis()
        return database.watchedPlateDao().insert(WatchedPlateEntity(normalizedPlate = plate, label = label?.trim()?.ifBlank { null }, notes = notes?.trim()?.ifBlank { null }, createdAtUtcMillis = now, updatedAtUtcMillis = now))
    }

    suspend fun registerDetection(
        rawText: String,
        confidence: Float,
        detectedAtUtcMillis: Long,
        matched: Boolean,
        cameraId: String? = null
    ): Long = database.withTransaction {
        val plate = normalizer.normalize(rawText)
        require(plate.isNotBlank()) { "La deteccion no contiene una placa valida" }
        val observedDao = database.observedPlateDao()
        val existing = observedDao.find(plate)
        val observedId = if (existing == null) {
            observedDao.insert(ObservedPlateEntity(normalizedPlate = plate, firstSeenAtUtcMillis = detectedAtUtcMillis, lastSeenAtUtcMillis = detectedAtUtcMillis))
        } else {
            observedDao.increment(existing.id, detectedAtUtcMillis)
            existing.id
        }
        database.detectionDao().insert(DetectionEntity(observedPlateId = observedId, normalizedPlate = plate, rawOcrText = rawText, confidence = confidence.coerceIn(0f, 1f), detectedAtUtcMillis = detectedAtUtcMillis, matched = matched, cameraId = cameraId))
    }

    suspend fun isWatched(rawPlate: String): Boolean =
        database.watchedPlateDao().findActive(normalizer.normalize(rawPlate)) != null
}
