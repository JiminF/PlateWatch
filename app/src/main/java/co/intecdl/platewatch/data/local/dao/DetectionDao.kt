package co.intecdl.platewatch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {
    @Insert
    suspend fun insert(entity: DetectionEntity): Long

    @Query("SELECT * FROM detections ORDER BY detectedAtUtcMillis DESC")
    fun observeAll(): Flow<List<DetectionEntity>>

    @Query("SELECT * FROM detections WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<DetectionEntity?>

    @Query("SELECT * FROM detections WHERE normalizedPlate = :plate ORDER BY detectedAtUtcMillis DESC")
    fun observeHistory(plate: String): Flow<List<DetectionEntity>>

    @Query("SELECT * FROM detections WHERE observedPlateId = :observedPlateId ORDER BY detectedAtUtcMillis DESC")
    fun observeHistoryByObservedPlate(observedPlateId: Long): Flow<List<DetectionEntity>>

    @Query("SELECT * FROM detections WHERE matched = :matched ORDER BY detectedAtUtcMillis DESC")
    fun observeByMatch(matched: Boolean): Flow<List<DetectionEntity>>

    @Query("DELETE FROM detections WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM detections WHERE observedPlateId = :observedPlateId")
    suspend fun deleteByObservedPlate(observedPlateId: Long)

    @Query("DELETE FROM detections")
    suspend fun deleteAll()
}
