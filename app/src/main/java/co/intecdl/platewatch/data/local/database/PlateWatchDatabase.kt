package co.intecdl.platewatch.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.intecdl.platewatch.data.local.dao.*
import co.intecdl.platewatch.data.local.entity.*

@Database(
    entities = [WatchedPlateEntity::class, ObservedPlateEntity::class, DetectionEntity::class, AppSettingsEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class PlateWatchDatabase : RoomDatabase() {
    abstract fun watchedPlateDao(): WatchedPlateDao
    abstract fun observedPlateDao(): ObservedPlateDao
    abstract fun detectionDao(): DetectionDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile private var instance: PlateWatchDatabase? = null
        fun getInstance(context: Context): PlateWatchDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, PlateWatchDatabase::class.java, "platewatch.db")
                .fallbackToDestructiveMigrationOnDowngrade(false)
                .build().also { instance = it }
        }
    }
}
