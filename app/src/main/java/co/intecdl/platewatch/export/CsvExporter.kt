package co.intecdl.platewatch.export
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import java.io.OutputStream
class CsvExporter { fun export(items:List<DetectionEntity>,output:OutputStream){ output.bufferedWriter().use{w->w.appendLine("plate,confidence,timestamp_utc,latitude,longitude,matched,original_image,stamped_image"); items.forEach{d->w.appendLine(listOf(d.normalizedPlate,d.confidence,d.detectedAtUtcMillis,d.latitude?:"",d.longitude?:"",d.matched,csv(d.originalImagePath),csv(d.stampedImagePath)).joinToString(","))}} }
 private fun csv(v:String?)=""${v.orEmpty().replace(""","""")}""
}
