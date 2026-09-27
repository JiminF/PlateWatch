package co.intecdl.platewatch.di

import android.content.Context
import co.intecdl.platewatch.backend.local.LocalBackend
import co.intecdl.platewatch.backend.local.RoomLocalBackend
import co.intecdl.platewatch.data.local.database.PlateWatchDatabase

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val database: PlateWatchDatabase by lazy { PlateWatchDatabase.getInstance(appContext) }
    val localBackend: LocalBackend by lazy {
        RoomLocalBackend(database, appContext.filesDir.resolve("PlateWatch"))
    }
}
