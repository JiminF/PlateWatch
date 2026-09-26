package co.intecdl.platewatch.camera.domain

import android.graphics.Bitmap
import android.util.Size

enum class CameraKind { INTERNAL, USB }
enum class CameraConnectionState { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

data class CameraDescriptor(
    val id: String,
    val displayName: String,
    val kind: CameraKind,
    val lensFacing: Int? = null,
    val isLogical: Boolean = false,
    val physicalCameraIds: Set<String> = emptySet()
)

data class CameraCapabilities(
    val resolutions: List<Size> = emptyList(),
    val fpsRanges: List<IntRange> = emptyList()
)

data class CameraFrame(val bitmap: Bitmap, val timestampNanos: Long, val rotationDegrees: Int)
