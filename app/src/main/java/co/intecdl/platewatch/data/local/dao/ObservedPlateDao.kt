package co.intecdl.platewatch.data.local.dao

import androidx.room.*
import co.intecdl.platewatch.data.local.entity.ObservedPlateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservedPlateDao {
    @Query("SELECT * FROM observed_plates ORDER BY lastSeenAtUtcMillis DESC")
    fun observeAll(): Flow<List<ObservedPlateEntity>>

    @Query("SELECT * FROM observed_plates WHERE normalizedPlate = :plate LIMIT 1")
    suspend fun find(plate: String): ObservedPlateEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ObservedPlateEntity): Long

    @Query("UPDATE observed_plates SET observationCount = observationCount + 1, lastSeenAtUtcMillis = :seenAt WHERE id = :id")
    suspend fun increment(id: Long, seenAt: Long)
}
