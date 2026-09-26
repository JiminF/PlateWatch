package co.intecdl.platewatch.camera.usb

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import co.intecdl.platewatch.camera.domain.UsbCameraDescriptor

class UsbCameraCatalog(context: Context) {
    private val usbManager = context.getSystemService(UsbManager::class.java)

    fun listProbableUvcCameras(): List<UsbCameraDescriptor> = usbManager.deviceList.values
        .map(::toDescriptor)
        .filter { it.isProbableUvcCamera }
        .sortedWith(compareBy({ it.vendorId }, { it.productId }, { it.deviceId }))

    fun find(deviceId: Int): UsbCameraDescriptor? =
        usbManager.deviceList.values.firstOrNull { it.deviceId == deviceId }?.let(::toDescriptor)

    private fun toDescriptor(device: UsbDevice): UsbCameraDescriptor {
        var videoControl = false
        var videoStreaming = false
        for (index in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(index)
            if (usbInterface.interfaceClass == UsbConstants.USB_CLASS_VIDEO) {
                when (usbInterface.interfaceSubclass) {
                    VIDEO_CONTROL_SUBCLASS -> videoControl = true
                    VIDEO_STREAMING_SUBCLASS -> videoStreaming = true
                    else -> videoStreaming = true
                }
            }
        }
        return UsbCameraDescriptor(
            deviceId = device.deviceId,
            deviceName = device.deviceName,
            vendorId = device.vendorId,
            productId = device.productId,
            manufacturerName = runCatching { device.manufacturerName }.getOrNull(),
            productName = runCatching { device.productName }.getOrNull(),
            hasVideoControlInterface = videoControl,
            hasVideoStreamingInterface = videoStreaming,
            permissionGranted = usbManager.hasPermission(device)
        )
    }

    private companion object {
        const val VIDEO_CONTROL_SUBCLASS = 1
        const val VIDEO_STREAMING_SUBCLASS = 2
    }
}
