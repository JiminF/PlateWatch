package co.intecdl.platewatch.watch
import co.intecdl.platewatch.alpr.pipeline.OperatingMode
data class ConfirmedReading(val plate:String,val confidence:Float,val timestampUtcMillis:Long)
data class WatchDecision(val register:Boolean,val alert:Boolean)
class WatchCoordinator(private val isWatched:suspend(String)->Boolean){ suspend fun decide(r:ConfirmedReading,mode:OperatingMode)=WatchDecision(register=mode!=OperatingMode.RECOGNIZE,alert=mode!=OperatingMode.REGISTER&&isWatched(r.plate)) }
