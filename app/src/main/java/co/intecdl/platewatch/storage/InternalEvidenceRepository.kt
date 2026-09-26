package co.intecdl.platewatch.storage
import android.content.Context
import android.graphics.*
import java.io.File
import java.time.*
import java.time.format.DateTimeFormatter
class InternalEvidenceRepository(private val context:Context):EvidenceRepository {
 override suspend fun save(request:EvidenceRequest,saveOriginal:Boolean,saveStamped:Boolean,quality:Int):EvidencePaths {
  val z=Instant.ofEpochMilli(request.detectedAtUtcMillis).atZone(ZoneId.systemDefault()); val dir=File(context.filesDir,"PlateWatch/detections/${z.year}/%02d/%02d".format(z.monthValue,z.dayOfMonth)).apply{mkdirs()}; val safe=request.plate.filter{it.isLetterOrDigit()}; val time=z.format(DateTimeFormatter.ofPattern("HHmmss")); var o:String?=null; var s:String?=null
  if(saveOriginal){ val f=File(dir,"${safe}_${time}_original.jpg"); f.outputStream().use{request.frame.compress(Bitmap.CompressFormat.JPEG,quality.coerceIn(1,100),it)}; o=f.absolutePath }
  if(saveStamped){ val copy=request.frame.copy(Bitmap.Config.ARGB_8888,true); val c=Canvas(copy); val p=Paint().apply{color=Color.WHITE;textSize=32f;setShadowLayer(4f,2f,2f,Color.BLACK)}; val loc=request.location?.let{"%.6f, %.6f".format(it.latitude,it.longitude)}?:"GPS no disponible"; listOf("PLACA: ${request.plate}","FECHA: ${z.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}","HORA: ${z.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}","GPS: $loc","CONFIANZA: ${(request.confidence*100).toInt()}%").forEachIndexed{i,t->c.drawText(t,24f,48f+i*40f,p)}; val f=File(dir,"${safe}_${time}_evidence.jpg"); f.outputStream().use{copy.compress(Bitmap.CompressFormat.JPEG,quality.coerceIn(1,100),it)}; s=f.absolutePath; copy.recycle() }
  return EvidencePaths(o,s)
 }
 override suspend fun delete(paths:EvidencePaths){ listOfNotNull(paths.original,paths.stamped).forEach{runCatching{File(it).delete()}} }
}
