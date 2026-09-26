package co.intecdl.platewatch.ui.observedplates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import co.intecdl.platewatch.data.local.database.PlateWatchDatabase
import co.intecdl.platewatch.data.repository.ObservedPlateRepository

@Composable
fun ObservedPlatesRoute(onExit: () -> Unit) {
    val context = LocalContext.current
    val database = PlateWatchDatabase.getInstance(context.applicationContext)
    val repository = ObservedPlateRepository(
        observedPlateDao = database.observedPlateDao(),
        detectionDao = database.detectionDao()
    )
    val viewModel: ObservedPlatesViewModel = viewModel(
        factory = ObservedPlatesViewModel.Factory(repository)
    )
    val state by viewModel.uiState.collectAsState()

    when (val destination = state.destination) {
        ObservedDestination.List -> ObservedPlatesScreen(
            state = state,
            onQueryChange = viewModel::setQuery,
            onOpenHistory = viewModel::openHistory,
            onBack = onExit
        )
        is ObservedDestination.History -> ObservedPlateHistoryRoute(
            observedPlateId = destination.observedPlateId,
            repository = repository,
            onOpenDetection = { detectionId ->
                viewModel.openDetection(destination.observedPlateId, detectionId)
            },
            onBack = { viewModel.back() }
        )
        is ObservedDestination.DetectionDetail -> DetectionDetailRoute(
            detectionId = destination.detectionId,
            repository = repository,
            onBack = { viewModel.back() }
        )
    }
}
