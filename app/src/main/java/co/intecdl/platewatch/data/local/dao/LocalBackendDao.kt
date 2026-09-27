package co.intecdl.platewatch.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import co.intecdl.platewatch.data.local.entity.DetectionEntity

@Dao
interface LocalBackendDao {
    @Query("SELECT * FROM detections WHERE id = :id LIMIT 1")
    suspend fun getDetection(id: Long): DetectionEntity?

    @Query("SELECT * FROM detections ORDER BY detectedAtUtcMillis ASC")
    suspend fun getAllDetectionsOnce(): List<DetectionEntity>

    @Query("DELETE FROM observed_plates")
    suspend fun clearObservedSummaries()
}
