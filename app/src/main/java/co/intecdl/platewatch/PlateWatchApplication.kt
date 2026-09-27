package co.intecdl.platewatch

import android.app.Application
import co.intecdl.platewatch.backend.local.RoomLocalBackend
import co.intecdl.platewatch.di.AppContainer
import co.intecdl.platewatch.notifications.AlertManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PlateWatchApplication : Application() {
    lateinit var container: AppContainer
        private set
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        AlertManager(this).createChannel()
        applicationScope.launch { (container.localBackend as? RoomLocalBackend)?.ensureDefaults() }
    }
}
