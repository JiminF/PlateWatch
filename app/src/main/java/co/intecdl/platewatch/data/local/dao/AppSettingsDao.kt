package co.intecdl.platewatch.data.local.dao

import androidx.room.*
import co.intecdl.platewatch.data.local.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE singletonId = 1")
    fun observe(): Flow<AppSettingsEntity?>
    @Query("SELECT * FROM app_settings WHERE singletonId = 1")
    suspend fun get(): AppSettingsEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(settings: AppSettingsEntity)
}
