package co.intecdl.platewatch.camera.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CameraSource {
    val state: StateFlow<CameraConnectionState>
    fun frames(): Flow<CameraFrame>
    suspend fun connect(cameraId: String)
    suspend fun disconnect()
    suspend fun captureImage(): Bitmap?
    suspend fun capabilities(): CameraCapabilities
}

interface CameraCatalog {
    suspend fun listCameras(): List<CameraDescriptor>
}
