package co.intecdl.platewatch.ui.watchedplates

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import co.intecdl.platewatch.data.local.database.PlateWatchDatabase
import co.intecdl.platewatch.data.repository.WatchedPlateRepository

@Composable
fun WatchedPlatesRoute(onBack: () -> Unit) {
    val context = LocalContext.current
    val database = PlateWatchDatabase.getInstance(context.applicationContext)
    val repository = WatchedPlateRepository(database.watchedPlateDao())
    val viewModel: WatchedPlatesViewModel = viewModel(
        factory = WatchedPlatesViewModel.Factory(repository)
    )
    WatchedPlatesScreen(viewModel = viewModel, onBack = onBack)
}
