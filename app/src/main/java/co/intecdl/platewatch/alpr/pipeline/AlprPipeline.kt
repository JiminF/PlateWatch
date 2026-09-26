package co.intecdl.platewatch.alpr.pipeline
import android.graphics.Bitmap
import co.intecdl.platewatch.alpr.core.PlateNormalizer

class AlprPipeline(private val preprocessor:FramePreprocessor, private val detector:PlateDetector, private val ocr:PlateOcr, private val normalizer:PlateNormalizer=PlateNormalizer()) {
 suspend fun process(frame:Bitmap):AlprOutcome {
  if(detector.state!=ModelState.READY || ocr.state!=ModelState.READY) return AlprOutcome.Unavailable("ALPR MODEL NOT AVAILABLE")
  return runCatching {
   val t0=System.nanoTime(); val p0=System.nanoTime(); val prepared=preprocessor.process(frame); val p=(System.nanoTime()-p0)/1_000_000
   val d0=System.nanoTime(); val regions=detector.detect(prepared); val d=(System.nanoTime()-d0)/1_000_000
   var ocrMs=0L
   val results=regions.mapNotNull { region -> val s=System.nanoTime(); val value=ocr.read(region.crop); ocrMs+=(System.nanoTime()-s)/1_000_000; value?.let { val normalized=normalizer.normalize(it.text); if(normalized.isBlank()) null else AlprResult(normalized,region.confidence,it.confidence,region.box,region.crop,0) } }
   val total=(System.nanoTime()-t0)/1_000_000
   AlprOutcome.Success(results.map{it.copy(totalLatencyMs=total)},PipelineMetrics(p,d,ocrMs,total))
  }.getOrElse { AlprOutcome.Failure(it.message ?: "PROCESSING ERROR") }
 }
}
