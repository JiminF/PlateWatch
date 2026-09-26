package co.intecdl.platewatch.data.local.database

import androidx.room.TypeConverter
import co.intecdl.platewatch.data.local.entity.OperatingMode

class RoomConverters {
    @TypeConverter fun operatingModeToString(value: OperatingMode): String = value.name
    @TypeConverter fun stringToOperatingMode(value: String): OperatingMode =
        runCatching { OperatingMode.valueOf(value) }.getOrDefault(OperatingMode.BOTH)
}
