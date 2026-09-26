package co.intecdl.platewatch.ui.detections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import co.intecdl.platewatch.data.local.database.PlateWatchDatabase
import co.intecdl.platewatch.data.repository.DetectionRepository
import co.intecdl.platewatch.ui.observedplates.DetectionDetailRoute
import co.intecdl.platewatch.data.repository.ObservedPlateRepository

@Composable
fun DetectionsRoute(onExit: () -> Unit) {
    val context = LocalContext.current
    val database = PlateWatchDatabase.getInstance(context.applicationContext)
    val repository = DetectionRepository(database.detectionDao())
    val viewModel: DetectionsViewModel = viewModel(factory = DetectionsViewModel.Factory(repository))
    val state by viewModel.uiState.collectAsState()

    when (val destination = state.destination) {
        DetectionsDestination.List -> DetectionsScreen(
            state = state,
            onQueryChange = viewModel::setQuery,
            onMatchFilterChange = viewModel::setMatchFilter,
            onDateFilterChange = viewModel::setDateFilter,
            onOpenDetail = viewModel::openDetail,
            onRequestDelete = viewModel::requestDelete,
            onRequestDeleteAll = viewModel::requestDeleteAll,
            onCancelDelete = viewModel::cancelDelete,
            onConfirmDelete = viewModel::confirmDelete,
            onCancelDeleteAll = viewModel::cancelDeleteAll,
            onConfirmDeleteAll = viewModel::confirmDeleteAll,
            onMessageShown = viewModel::clearMessage,
            onBack = onExit
        )
        is DetectionsDestination.Detail -> {
            val observedRepository = ObservedPlateRepository(database.observedPlateDao(), database.detectionDao())
            DetectionDetailRoute(
                detectionId = destination.id,
                repository = observedRepository,
                onBack = { viewModel.back() }
            )
        }
    }
}
