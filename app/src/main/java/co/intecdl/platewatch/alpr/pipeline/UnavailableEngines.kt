package co.intecdl.platewatch.alpr.pipeline
import android.graphics.Bitmap
class IdentityPreprocessor:FramePreprocessor { override suspend fun process(frame:Bitmap)=frame }
class UnavailableDetector:PlateDetector { override val state=ModelState.NOT_AVAILABLE; override suspend fun detect(frame:Bitmap)=emptyList<PlateRegion>() }
class UnavailableOcr:PlateOcr { override val state=ModelState.NOT_AVAILABLE; override suspend fun read(crop:Bitmap):OcrReading?=null }
