package co.intecdl.platewatch.ui.observedplates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.intecdl.platewatch.data.local.entity.ObservedPlateEntity
import co.intecdl.platewatch.data.repository.ObservedPlateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

sealed interface ObservedDestination {
    data object List : ObservedDestination
    data class History(val observedPlateId: Long) : ObservedDestination
    data class DetectionDetail(val observedPlateId: Long, val detectionId: Long) : ObservedDestination
}

data class ObservedPlatesUiState(
    val query: String = "",
    val plates: List<ObservedPlateEntity> = emptyList(),
    val destination: ObservedDestination = ObservedDestination.List
)

@OptIn(ExperimentalCoroutinesApi::class)
class ObservedPlatesViewModel(
    private val repository: ObservedPlateRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val destination = MutableStateFlow<ObservedDestination>(ObservedDestination.List)

    private val plates = query
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { value ->
            if (value.isBlank()) repository.observeAll() else repository.search(value)
        }

    val uiState: StateFlow<ObservedPlatesUiState> = combine(query, plates, destination) {
            currentQuery, currentPlates, currentDestination ->
        ObservedPlatesUiState(currentQuery, currentPlates, currentDestination)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ObservedPlatesUiState()
    )

    fun setQuery(value: String) { query.value = value }
    fun openHistory(id: Long) { destination.value = ObservedDestination.History(id) }
    fun openDetection(observedPlateId: Long, detectionId: Long) {
        destination.value = ObservedDestination.DetectionDetail(observedPlateId, detectionId)
    }
    fun back(): Boolean = when (val current = destination.value) {
        ObservedDestination.List -> false
        is ObservedDestination.History -> {
            destination.value = ObservedDestination.List
            true
        }
        is ObservedDestination.DetectionDetail -> {
            destination.value = ObservedDestination.History(current.observedPlateId)
            true
        }
    }

    class Factory(private val repository: ObservedPlateRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ObservedPlatesViewModel(repository) as T
    }
}
