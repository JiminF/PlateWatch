package co.intecdl.platewatch.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "observed_plates",
    indices = [Index(value = ["normalizedPlate"], unique = true), Index(value = ["lastSeenAtUtcMillis"])]
)
data class ObservedPlateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val normalizedPlate: String,
    val observationCount: Long = 1,
    val firstSeenAtUtcMillis: Long,
    val lastSeenAtUtcMillis: Long
)
