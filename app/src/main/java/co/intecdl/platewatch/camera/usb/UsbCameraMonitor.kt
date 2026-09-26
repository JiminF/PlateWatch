package co.intecdl.platewatch.camera.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import co.intecdl.platewatch.camera.domain.UsbCameraEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UsbCameraMonitor(private val context: Context) : AutoCloseable {
    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(UsbManager::class.java)
    private val catalog = UsbCameraCatalog(appContext)
    private val _events = MutableSharedFlow<UsbCameraEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<UsbCameraEvent> = _events.asSharedFlow()
    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.usbDevice()
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    emit(UsbCameraEvent.Type.ATTACHED, device)
                    emit(UsbCameraEvent.Type.INVENTORY_CHANGED, null)
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    emit(UsbCameraEvent.Type.DETACHED, device)
                    emit(UsbCameraEvent.Type.INVENTORY_CHANGED, null)
                }
                ACTION_USB_PERMISSION -> {
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    emit(if (granted) UsbCameraEvent.Type.PERMISSION_GRANTED else UsbCameraEvent.Type.PERMISSION_DENIED, device)
                }
            }
        }
    }

    fun start() {
        if (registered) return
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(ACTION_USB_PERMISSION)
        }
        if (Build.VERSION.SDK_INT >= 33) appContext.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        else @Suppress("DEPRECATION") appContext.registerReceiver(receiver, filter)
        registered = true
    }

    fun requestPermission(deviceId: Int): Boolean {
        val device = manager.deviceList.values.firstOrNull { it.deviceId == deviceId } ?: return false
        if (manager.hasPermission(device)) {
            emit(UsbCameraEvent.Type.PERMISSION_GRANTED, device)
            return true
        }
        val intent = PendingIntent.getBroadcast(
            appContext,
            deviceId,
            Intent(ACTION_USB_PERMISSION).setPackage(appContext.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        manager.requestPermission(device, intent)
        return true
    }

    override fun close() {
        if (!registered) return
        runCatching { appContext.unregisterReceiver(receiver) }
        registered = false
    }

    private fun emit(type: UsbCameraEvent.Type, device: UsbDevice?) {
        val descriptor = device?.let { catalog.find(it.deviceId) }
        _events.tryEmit(UsbCameraEvent(type, descriptor))
    }

    @Suppress("DEPRECATION")
    private fun Intent.usbDevice(): UsbDevice? =
        if (Build.VERSION.SDK_INT >= 33) getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
        else getParcelableExtra(UsbManager.EXTRA_DEVICE)

    companion object {
        const val ACTION_USB_PERMISSION = "co.intecdl.platewatch.USB_PERMISSION"
    }
}
