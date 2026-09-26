package co.intecdl.platewatch.location
data class DeviceLocation(val latitude:Double,val longitude:Double,val accuracyMeters:Float,val timestampUtcMillis:Long)
interface LocationProvider { suspend fun currentOrLast():DeviceLocation? }
