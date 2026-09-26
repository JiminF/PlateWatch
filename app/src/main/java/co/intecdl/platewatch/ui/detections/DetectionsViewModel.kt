package co.intecdl.platewatch.ui.detections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.intecdl.platewatch.data.local.entity.DetectionEntity
import co.intecdl.platewatch.data.repository.DetectionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DetectionsDestination {
    data object List : DetectionsDestination
    data class Detail(val id: Long) : DetectionsDestination
}

data class DetectionsUiState(
    val query: String = "",
    val matchFilter: MatchFilter = MatchFilter.ALL,
    val dateFilter: DateFilter = DateFilter.ALL,
    val detections: List<DetectionEntity> = emptyList(),
    val totalCount: Long = 0,
    val matchedCount: Long = 0,
    val deleteCandidate: DetectionEntity? = null,
    val confirmDeleteAll: Boolean = false,
    val destination: DetectionsDestination = DetectionsDestination.List,
    val message: String? = null
)

private data class FilterState(
    val query: String,
    val matchFilter: MatchFilter,
    val dateFilter: DateFilter
)

@OptIn(ExperimentalCoroutinesApi::class)
class DetectionsViewModel(private val repository: DetectionRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val matchFilter = MutableStateFlow(MatchFilter.ALL)
    private val dateFilter = MutableStateFlow(DateFilter.ALL)
    private val deleteCandidate = MutableStateFlow<DetectionEntity?>(null)
    private val confirmDeleteAll = MutableStateFlow(false)
    private val destination = MutableStateFlow<DetectionsDestination>(DetectionsDestination.List)
    private val message = MutableStateFlow<String?>(null)

    private val filters = combine(query.debounce(250), matchFilter, dateFilter) { q, match, date ->
        FilterState(q, match, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FilterState("", MatchFilter.ALL, DateFilter.ALL))

    private val detections = filters.flatMapLatest { filter ->
        val range = filter.dateFilter.toUtcRange()
        repository.observeFiltered(filter.query, filter.matchFilter.databaseValue, range.start, range.endExclusive)
    }

    private val counts = dateFilter.flatMapLatest { date ->
        val range = date.toUtcRange()
        combine(
            repository.observeCount(range.start, range.endExclusive),
            repository.observeMatchedCount(range.start, range.endExclusive)
        ) { total, matched -> total to matched }
    }

    val uiState: StateFlow<DetectionsUiState> = combine(
        query, matchFilter, dateFilter, detections, counts,
        deleteCandidate, confirmDeleteAll, destination, message
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        DetectionsUiState(
            query = values[0] as String,
            matchFilter = values[1] as MatchFilter,
            dateFilter = values[2] as DateFilter,
            detections = values[3] as List<DetectionEntity>,
            totalCount = (values[4] as Pair<Long, Long>).first,
            matchedCount = (values[4] as Pair<Long, Long>).second,
            deleteCandidate = values[5] as DetectionEntity?,
            confirmDeleteAll = values[6] as Boolean,
            destination = values[7] as DetectionsDestination,
            message = values[8] as String?
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetectionsUiState())

    fun setQuery(value: String) { query.value = value }
    fun setMatchFilter(value: MatchFilter) { matchFilter.value = value }
    fun setDateFilter(value: DateFilter) { dateFilter.value = value }
    fun openDetail(id: Long) { destination.value = DetectionsDestination.Detail(id) }
    fun back(): Boolean = if (destination.value is DetectionsDestination.Detail) {
        destination.value = DetectionsDestination.List
        true
    } else false
    fun requestDelete(entity: DetectionEntity) { deleteCandidate.value = entity }
    fun cancelDelete() { deleteCandidate.value = null }
    fun requestDeleteAll() { confirmDeleteAll.value = true }
    fun cancelDeleteAll() { confirmDeleteAll.value = false }
    fun clearMessage() { message.value = null }

    fun confirmDelete() {
        val entity = deleteCandidate.value ?: return
        viewModelScope.launch {
            repository.delete(entity.id)
            deleteCandidate.value = null
            message.value = "Deteccion eliminada"
        }
    }

    fun confirmDeleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            confirmDeleteAll.value = false
            message.value = "Historial de detecciones eliminado"
        }
    }

    class Factory(private val repository: DetectionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetectionsViewModel(repository) as T
    }
}
