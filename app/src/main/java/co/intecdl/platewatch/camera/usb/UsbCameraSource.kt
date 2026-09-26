package co.intecdl.platewatch.camera.usb

import android.graphics.Bitmap
import co.intecdl.platewatch.camera.domain.CameraCapabilities
import co.intecdl.platewatch.camera.domain.CameraConnectionState
import co.intecdl.platewatch.camera.domain.CameraFrame
import co.intecdl.platewatch.camera.domain.CameraSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Frontera de integracion UVC. No abre video hasta conectar un backend AUSBC validado.
 * Mantener esta clase evita que UI y ALPR dependan directamente de una libreria externa.
 */
class UsbCameraSource : CameraSource {
    private val _state = MutableStateFlow(CameraConnectionState.DISCONNECTED)
    override val state: StateFlow<CameraConnectionState> = _state
    override fun frames(): Flow<CameraFrame> = emptyFlow()
    override suspend fun connect(cameraId: String) {
        _state.value = CameraConnectionState.ERROR
        throw IllegalStateException("UVC BACKEND NOT AVAILABLE")
    }
    override suspend fun disconnect() { _state.value = CameraConnectionState.DISCONNECTED }
    override suspend fun captureImage(): Bitmap? = null
    override suspend fun capabilities(): CameraCapabilities = CameraCapabilities()
}
