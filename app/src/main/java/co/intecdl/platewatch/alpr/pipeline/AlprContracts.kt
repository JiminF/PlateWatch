package co.intecdl.platewatch.alpr.pipeline
import android.graphics.Bitmap
interface PlateDetector { val state: ModelState; suspend fun detect(frame: Bitmap): List<PlateRegion> }
interface PlateOcr { val state: ModelState; suspend fun read(crop: Bitmap): OcrReading? }
interface FramePreprocessor { suspend fun process(frame: Bitmap): Bitmap }
