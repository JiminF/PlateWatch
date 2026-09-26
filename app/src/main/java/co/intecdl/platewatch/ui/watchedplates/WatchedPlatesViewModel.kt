package co.intecdl.platewatch.ui.watchedplates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.intecdl.platewatch.data.local.entity.WatchedPlateEntity
import co.intecdl.platewatch.data.repository.WatchedPlateRepository
import co.intecdl.platewatch.data.repository.WatchedPlateSaveResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface WatchedPlateMessage {
    data object Saved : WatchedPlateMessage
    data object Deleted : WatchedPlateMessage
    data object Duplicate : WatchedPlateMessage
    data object InvalidPlate : WatchedPlateMessage
    data object NotFound : WatchedPlateMessage
}

data class WatchedPlatesUiState(
    val query: String = "",
    val plates: List<WatchedPlateEntity> = emptyList(),
    val editor: WatchedPlateEditorState? = null,
    val deleteCandidate: WatchedPlateEntity? = null,
    val message: WatchedPlateMessage? = null
)

data class WatchedPlateEditorState(
    val id: Long? = null,
    val plate: String = "",
    val label: String = "",
    val notes: String = "",
    val active: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class WatchedPlatesViewModel(
    private val repository: WatchedPlateRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val editor = MutableStateFlow<WatchedPlateEditorState?>(null)
    private val deleteCandidate = MutableStateFlow<WatchedPlateEntity?>(null)
    private val message = MutableStateFlow<WatchedPlateMessage?>(null)

    private val plates = query
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { value -> if (value.isBlank()) repository.observeAll() else repository.search(value) }

    val uiState: StateFlow<WatchedPlatesUiState> = combine(
        query, plates, editor, deleteCandidate, message
    ) { currentQuery, currentPlates, currentEditor, currentDelete, currentMessage ->
        WatchedPlatesUiState(currentQuery, currentPlates, currentEditor, currentDelete, currentMessage)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WatchedPlatesUiState())

    fun setQuery(value: String) { query.value = value }
    fun add() { editor.value = WatchedPlateEditorState() }
    fun edit(entity: WatchedPlateEntity) {
        editor.value = WatchedPlateEditorState(entity.id, entity.normalizedPlate, entity.label.orEmpty(), entity.notes.orEmpty(), entity.active)
    }
    fun closeEditor() { editor.value = null }
    fun updateEditor(value: WatchedPlateEditorState) { editor.value = value }
    fun requestDelete(entity: WatchedPlateEntity) { deleteCandidate.value = entity }
    fun cancelDelete() { deleteCandidate.value = null }
    fun clearMessage() { message.value = null }

    fun save() {
        val value = editor.value ?: return
        viewModelScope.launch {
            val result = if (value.id == null) {
                repository.create(value.plate, value.label, value.notes, value.active)
            } else {
                repository.update(value.id, value.plate, value.label, value.notes, value.active)
            }
            when (result) {
                is WatchedPlateSaveResult.Success -> {
                    editor.value = null
                    message.value = WatchedPlateMessage.Saved
                }
                WatchedPlateSaveResult.Duplicate -> message.value = WatchedPlateMessage.Duplicate
                WatchedPlateSaveResult.InvalidPlate -> message.value = WatchedPlateMessage.InvalidPlate
                WatchedPlateSaveResult.NotFound -> message.value = WatchedPlateMessage.NotFound
            }
        }
    }

    fun setActive(entity: WatchedPlateEntity, active: Boolean) {
        viewModelScope.launch { repository.setActive(entity.id, active) }
    }

    fun confirmDelete() {
        val entity = deleteCandidate.value ?: return
        viewModelScope.launch {
            repository.delete(entity.id)
            deleteCandidate.value = null
            message.value = WatchedPlateMessage.Deleted
        }
    }

    class Factory(private val repository: WatchedPlateRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            WatchedPlatesViewModel(repository) as T
    }
}
