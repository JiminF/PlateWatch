package co.intecdl.platewatch.alpr.core
import java.util.ArrayDeque
data class PlateReading(val plate:String,val confidence:Float,val timestampMillis:Long)
data class ConfirmedPlate(val plate:String,val confidence:Float,val confirmedAtMillis:Long)
class DetectionStabilizer(private val minimumConfidence:Float=.85f,private val requiredReadings:Int=3,private val windowMillis:Long=2000,private val cooldownMillis:Long=10000){
 private val readings=ArrayDeque<PlateReading>(); private val last=mutableMapOf<String,Long>()
 @Synchronized fun add(r:PlateReading):ConfirmedPlate?{ if(r.confidence<minimumConfidence||r.plate.isBlank())return null; while(readings.isNotEmpty()&&r.timestampMillis-readings.first.timestampMillis>windowMillis)readings.removeFirst(); readings.addLast(r); val m=readings.filter{it.plate==r.plate}; if(m.size<requiredReadings)return null; val p=last[r.plate]; if(p!=null&&r.timestampMillis-p<cooldownMillis)return null; last[r.plate]=r.timestampMillis; return ConfirmedPlate(r.plate,m.maxOf{it.confidence},r.timestampMillis) }
}
