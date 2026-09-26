package co.intecdl.platewatch.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watched_plates",
    indices = [Index(value = ["normalizedPlate"], unique = true), Index(value = ["active"])]
)
data class WatchedPlateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val normalizedPlate: String,
    val label: String? = null,
    val notes: String? = null,
    val active: Boolean = true,
    val createdAtUtcMillis: Long,
    val updatedAtUtcMillis: Long
)
