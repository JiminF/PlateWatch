package co.intecdl.platewatch.backend.local

import androidx.room.withTransaction
import co.intecdl.platewatch.alpr.core.PlateNormalizer
import co.intecdl.platewatch.data.local.database.PlateWatchDatabase
import co.intecdl.platewatch.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import java.io.File

class RoomLocalBackend(
    private val database: PlateWatchDatabase,
    private val filesRoot: File,
    private val normalizer: PlateNormalizer = PlateNormalizer()
) : LocalBackend {
    private val _status = MutableStateFlow(
        LocalBackendStatus(true, filesRoot.exists() || filesRoot.mkdirs(), false, "BACKEND LOCAL ACTIVO | ALPR MODEL NOT AVAILABLE")
    )
    override val status: Flow<LocalBackendStatus> = _status

    override fun watchedPlates(): Flow<List<WatchedPlateEntity>> = database.watchedPlateDao().observeAll()
    override fun observedPlates(): Flow<List<ObservedPlateEntity>> = database.observedPlateDao().observeAll()
    override fun detections(): Flow<List<DetectionEntity>> = database.detectionDao().observeAll()
    override fun settings(): Flow<AppSettingsEntity> = database.appSettingsDao().observe().filterNotNull()

    suspend fun ensureDefaults() {
        if (database.appSettingsDao().get() == null) database.appSettingsDao().save(AppSettingsEntity())
    }

    override suspend fun processConfirmedDetection(input: ConfirmedDetectionInput): DetectionProcessingResult = database.withTransaction {
        val plate = normalizer.normalize(input.rawPlate)
        require(plate.isNotBlank()) { "La placa confirmada no puede quedar vacia" }
        val settings = database.appSettingsDao().get() ?: AppSettingsEntity().also { database.appSettingsDao().save(it) }
        val watched = database.watchedPlateDao().findActive(plate) != null
        val register = settings.operatingMode != OperatingMode.RECOGNIZE
        val alert = watched && settings.operatingMode != OperatingMode.REGISTER
        var detectionId: Long? = null

        if (register || alert) {
            val observedDao = database.observedPlateDao()
            val existing = observedDao.find(plate)
            val observedId = if (existing == null) {
                observedDao.insert(
                    ObservedPlateEntity(
                        normalizedPlate = plate,
                        firstSeenAtUtcMillis = input.detectedAtUtcMillis,
                        lastSeenAtUtcMillis = input.detectedAtUtcMillis
                    )
                )
            } else {
                observedDao.increment(existing.id, input.detectedAtUtcMillis)
                existing.id
            }
            detectionId = database.detectionDao().insert(
                DetectionEntity(
                    observedPlateId = observedId,
                    normalizedPlate = plate,
                    rawOcrText = input.rawPlate,
                    confidence = input.confidence.coerceIn(0f, 1f),
                    latitude = input.latitude,
                    longitude = input.longitude,
                    accuracyMeters = input.accuracyMeters,
                    locationTimestampUtcMillis = input.locationTimestampUtcMillis,
                    detectedAtUtcMillis = input.detectedAtUtcMillis,
                    originalImagePath = input.originalImagePath,
                    stampedImagePath = input.stampedImagePath,
                    matched = watched,
                    cameraId = input.cameraId
                )
            )
        }
        DetectionProcessingResult(detectionId, plate, watched, register, alert, settings.operatingMode)
    }

    override suspend fun saveSettings(settings: AppSettingsEntity) = database.appSettingsDao().save(settings.copy(singletonId = 1))

    override suspend fun deleteDetection(id: Long) = database.withTransaction {
        val item = database.localBackendDao().getDetection(id) ?: return@withTransaction
        listOfNotNull(item.originalImagePath, item.stampedImagePath).forEach { runCatching { File(it).delete() } }
        database.detectionDao().deleteById(id)
        rebuildObservedSummariesInsideTransaction()
    }

    override suspend fun deleteAllDetections() = database.withTransaction {
        database.localBackendDao().getAllDetectionsOnce().flatMap { listOfNotNull(it.originalImagePath, it.stampedImagePath) }
            .forEach { runCatching { File(it).delete() } }
        database.detectionDao().deleteAll()
        database.localBackendDao().clearObservedSummaries()
    }

    override suspend fun rebuildObservedSummaries() = database.withTransaction { rebuildObservedSummariesInsideTransaction() }

    private suspend fun rebuildObservedSummariesInsideTransaction() {
        val events = database.localBackendDao().getAllDetectionsOnce()
        database.localBackendDao().clearObservedSummaries()
        events.groupBy { it.normalizedPlate }.forEach { (plate, items) ->
            val ordered = items.sortedBy { it.detectedAtUtcMillis }
            val id = database.observedPlateDao().insert(
                ObservedPlateEntity(
                    normalizedPlate = plate,
                    observationCount = ordered.size.toLong(),
                    firstSeenAtUtcMillis = ordered.first().detectedAtUtcMillis,
                    lastSeenAtUtcMillis = ordered.last().detectedAtUtcMillis
                )
            )
            // Los eventos conservan su historial por placa aun si su observedPlateId anterior cambia.
            // Una migracion futura puede actualizar la FK si se activa integridad referencial estricta.
        }
    }
}
