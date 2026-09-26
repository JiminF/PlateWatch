package co.intecdl.platewatch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.intecdl.platewatch.data.local.entity.ObservedPlateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservedPlateDao {
    @Query("SELECT * FROM observed_plates ORDER BY lastSeenAtUtcMillis DESC")
    fun observeAll(): Flow<List<ObservedPlateEntity>>

    @Query(
        """
        SELECT * FROM observed_plates
        WHERE normalizedPlate LIKE '%' || :query || '%'
        ORDER BY lastSeenAtUtcMillis DESC
        """
    )
    fun search(query: String): Flow<List<ObservedPlateEntity>>

    @Query("SELECT * FROM observed_plates WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ObservedPlateEntity?>

    @Query("SELECT * FROM observed_plates WHERE normalizedPlate = :plate LIMIT 1")
    suspend fun find(plate: String): ObservedPlateEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ObservedPlateEntity): Long

    @Query(
        """
        UPDATE observed_plates
        SET observationCount = observationCount + 1,
            lastSeenAtUtcMillis = :seenAt
        WHERE id = :id
        """
    )
    suspend fun increment(id: Long, seenAt: Long)

    @Query("DELETE FROM observed_plates WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM observed_plates")
    suspend fun deleteAll()
}
