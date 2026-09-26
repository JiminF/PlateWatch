package co.intecdl.platewatch.camera.internal

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import co.intecdl.platewatch.camera.domain.CameraCatalog
import co.intecdl.platewatch.camera.domain.CameraDescriptor
import co.intecdl.platewatch.camera.domain.CameraKind

class InternalCameraCatalog(context: Context) : CameraCatalog {
    private val manager = context.getSystemService(CameraManager::class.java)

    override suspend fun listCameras(): List<CameraDescriptor> = manager.cameraIdList.map { id ->
        val c = manager.getCameraCharacteristics(id)
        val facing = c.get(CameraCharacteristics.LENS_FACING)
        val capabilities = c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES).orEmpty()
        val logical = capabilities.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA)
        val label = when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT -> "Camara frontal ($id)"
            CameraCharacteristics.LENS_FACING_BACK -> "Camara trasera ($id)"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "Camara externa ($id)"
            else -> "Camara interna ($id)"
        }
        CameraDescriptor(id, label, CameraKind.INTERNAL, facing, logical, c.physicalCameraIds)
    }
}
