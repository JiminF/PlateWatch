package co.intecdl.platewatch.camera.internal
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import co.intecdl.platewatch.camera.domain.*
class InternalCameraCatalog(context:Context){
 private val manager=context.getSystemService(CameraManager::class.java)
 fun list():List<CameraDescriptor> = manager.cameraIdList.map { id ->
  val c=manager.getCameraCharacteristics(id); val facing=c.get(CameraCharacteristics.LENS_FACING)
  val logical=c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES).orEmpty().contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA)
  val side=when(facing){CameraCharacteristics.LENS_FACING_FRONT->"Frontal";CameraCharacteristics.LENS_FACING_BACK->"Trasera";CameraCharacteristics.LENS_FACING_EXTERNAL->"Externa";else->"Camara"}
  CameraDescriptor(id,"$side ($id)",CameraKind.INTERNAL,logical,c.physicalCameraIds)
 }
}
