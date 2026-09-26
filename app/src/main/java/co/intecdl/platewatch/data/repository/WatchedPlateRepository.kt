package co.intecdl.platewatch.data.repository

import android.database.sqlite.SQLiteConstraintException
import co.intecdl.platewatch.alpr.core.PlateNormalizer
import co.intecdl.platewatch.data.local.dao.WatchedPlateDao
import co.intecdl.platewatch.data.local.entity.WatchedPlateEntity
import kotlinx.coroutines.flow.Flow

sealed interface WatchedPlateSaveResult {
    data class Success(val id: Long) : WatchedPlateSaveResult
    data object Duplicate : WatchedPlateSaveResult
    data object InvalidPlate : WatchedPlateSaveResult
    data object NotFound : WatchedPlateSaveResult
}

class WatchedPlateRepository(
    private val dao: WatchedPlateDao,
    private val normalizer: PlateNormalizer = PlateNormalizer()
) {
    fun observeAll(): Flow<List<WatchedPlateEntity>> = dao.observeAll()

    fun search(rawQuery: String): Flow<List<WatchedPlateEntity>> =
        dao.search(normalizer.normalize(rawQuery).ifBlank { rawQuery.trim() })

    suspend fun create(
        rawPlate: String,
        label: String?,
        notes: String?,
        active: Boolean
    ): WatchedPlateSaveResult {
        val normalized = normalizer.normalize(rawPlate)
        if (normalized.isBlank()) return WatchedPlateSaveResult.InvalidPlate
        if (dao.findByPlate(normalized) != null) return WatchedPlateSaveResult.Duplicate
        val now = System.currentTimeMillis()
        return try {
            val id = dao.insert(
                WatchedPlateEntity(
                    normalizedPlate = normalized,
                    label = label.clean(),
                    notes = notes.clean(),
                    active = active,
                    createdAtUtcMillis = now,
                    updatedAtUtcMillis = now
                )
            )
            WatchedPlateSaveResult.Success(id)
        } catch (_: SQLiteConstraintException) {
            WatchedPlateSaveResult.Duplicate
        }
    }

    suspend fun update(
        id: Long,
        rawPlate: String,
        label: String?,
        notes: String?,
        active: Boolean
    ): WatchedPlateSaveResult {
        val current = dao.findById(id) ?: return WatchedPlateSaveResult.NotFound
        val normalized = normalizer.normalize(rawPlate)
        if (normalized.isBlank()) return WatchedPlateSaveResult.InvalidPlate
        val duplicate = dao.findByPlate(normalized)
        if (duplicate != null && duplicate.id != id) return WatchedPlateSaveResult.Duplicate
        return try {
            dao.update(
                current.copy(
                    normalizedPlate = normalized,
                    label = label.clean(),
                    notes = notes.clean(),
                    active = active,
                    updatedAtUtcMillis = System.currentTimeMillis()
                )
            )
            WatchedPlateSaveResult.Success(id)
        } catch (_: SQLiteConstraintException) {
            WatchedPlateSaveResult.Duplicate
        }
    }

    suspend fun setActive(id: Long, active: Boolean) =
        dao.setActive(id, active, System.currentTimeMillis())

    suspend fun delete(id: Long) = dao.deleteById(id)

    private fun String?.clean(): String? = this?.trim()?.ifBlank { null }
}
