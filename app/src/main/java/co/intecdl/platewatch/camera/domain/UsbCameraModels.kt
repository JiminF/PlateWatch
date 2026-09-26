package co.intecdl.platewatch.camera.domain

enum class UsbPermissionState { NOT_REQUIRED, REQUIRED, REQUESTING, GRANTED, DENIED }
enum class UsbConnectionState { DISCONNECTED, CONNECTED, PERMISSION_REQUIRED, READY, ERROR }

data class UsbCameraDescriptor(
    val deviceId: Int,
    val deviceName: String,
    val vendorId: Int,
    val productId: Int,
    val manufacturerName: String?,
    val productName: String?,
    val hasVideoControlInterface: Boolean,
    val hasVideoStreamingInterface: Boolean,
    val permissionGranted: Boolean
) {
    val stableId: String get() = "usb:$vendorId:$productId:$deviceId"
    val isProbableUvcCamera: Boolean
        get() = hasVideoControlInterface || hasVideoStreamingInterface
}

data class UsbCameraEvent(
    val type: Type,
    val camera: UsbCameraDescriptor?,
    val message: String? = null
) {
    enum class Type { ATTACHED, DETACHED, PERMISSION_GRANTED, PERMISSION_DENIED, INVENTORY_CHANGED, ERROR }
}
