package co.intecdl.platewatch.data.repository

import co.intecdl.platewatch.alpr.core.PlateNormalizer
import co.intecdl.platewatch.data.local.dao.DetectionDao
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import kotlinx.coroutines.flow.Flow

class DetectionRepository(
    private val dao: DetectionDao,
    private val normalizer: PlateNormalizer = PlateNormalizer()
) {
    fun observeFiltered(
        rawQuery: String,
        matched: Boolean?,
        startUtcMillis: Long?,
        endUtcMillis: Long?
    ): Flow<List<DetectionEntity>> = dao.observeFiltered(
        query = normalizer.normalize(rawQuery),
        matchedFilter = matched,
        startUtcMillis = startUtcMillis,
        endUtcMillis = endUtcMillis
    )

    fun observeCount(startUtcMillis: Long?, endUtcMillis: Long?): Flow<Long> =
        dao.observeCount(startUtcMillis, endUtcMillis)

    fun observeMatchedCount(startUtcMillis: Long?, endUtcMillis: Long?): Flow<Long> =
        dao.observeMatchedCount(startUtcMillis, endUtcMillis)

    fun observeById(id: Long): Flow<DetectionEntity?> = dao.observeById(id)
    suspend fun delete(id: Long) = dao.deleteById(id)
    suspend fun deleteAll() = dao.deleteAll()
}
