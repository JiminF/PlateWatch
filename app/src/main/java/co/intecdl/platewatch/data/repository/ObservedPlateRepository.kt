package co.intecdl.platewatch.data.repository

import co.intecdl.platewatch.alpr.core.PlateNormalizer
import co.intecdl.platewatch.data.local.dao.DetectionDao
import co.intecdl.platewatch.data.local.dao.ObservedPlateDao
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import co.intecdl.platewatch.data.local.entity.ObservedPlateEntity
import kotlinx.coroutines.flow.Flow

class ObservedPlateRepository(
    private val observedPlateDao: ObservedPlateDao,
    private val detectionDao: DetectionDao,
    private val normalizer: PlateNormalizer = PlateNormalizer()
) {
    fun observeAll(): Flow<List<ObservedPlateEntity>> = observedPlateDao.observeAll()

    fun search(rawQuery: String): Flow<List<ObservedPlateEntity>> =
        observedPlateDao.search(normalizer.normalize(rawQuery))

    fun observePlate(id: Long): Flow<ObservedPlateEntity?> =
        observedPlateDao.observeById(id)

    fun observeHistory(observedPlateId: Long): Flow<List<DetectionEntity>> =
        detectionDao.observeHistoryByObservedPlate(observedPlateId)

    fun observeDetection(id: Long): Flow<DetectionEntity?> =
        detectionDao.observeById(id)
}
