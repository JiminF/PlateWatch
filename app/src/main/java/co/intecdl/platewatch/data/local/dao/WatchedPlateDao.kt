package co.intecdl.platewatch.data.local.dao

import androidx.room.*
import co.intecdl.platewatch.data.local.entity.WatchedPlateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedPlateDao {
    @Query("SELECT * FROM watched_plates ORDER BY normalizedPlate")
    fun observeAll(): Flow<List<WatchedPlateEntity>>

    @Query("SELECT * FROM watched_plates WHERE normalizedPlate LIKE '%' || :query || '%' OR label LIKE '%' || :query || '%' ORDER BY normalizedPlate")
    fun search(query: String): Flow<List<WatchedPlateEntity>>

    @Query("SELECT * FROM watched_plates WHERE normalizedPlate = :plate AND active = 1 LIMIT 1")
    suspend fun findActive(plate: String): WatchedPlateEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: WatchedPlateEntity): Long

    @Update suspend fun update(entity: WatchedPlateEntity)
    @Delete suspend fun delete(entity: WatchedPlateEntity)
    @Query("UPDATE watched_plates SET active = :active, updatedAtUtcMillis = :updatedAt WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean, updatedAt: Long)
    @Query("SELECT COUNT(*) FROM watched_plates WHERE active = 1")
    fun observeActiveCount(): Flow<Long>
}
