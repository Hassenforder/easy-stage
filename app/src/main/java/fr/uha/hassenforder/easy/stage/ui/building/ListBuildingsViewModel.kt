package fr.uha.hassenforder.easy.stage.ui.building

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.database.model.ManagementState
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.navigation.BuildingsDestination
import fr.uha.hassenforder.easy.stage.repository.BuildingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListBuildingsViewModel @Inject constructor (
    private val repository: BuildingRepository,
    private val navigator: Navigator
): ViewModel() {

    val titleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = titleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _listMode = MutableStateFlow(ListMode.FILTERED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _buildings: Flow<Result<List<BuildingWithDetails>>> = _listMode
        .flatMapLatest { m ->
            when(m) {
                ListMode.ALL -> repository.getAllWithDetails()
                ListMode.FILTERED -> repository.getFilteredWithDetails()
            }
        }
        .map { content -> Result.Success(content) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    @Immutable
    data class UIState (
        val uiTitle: UITitle,
        val listMode : ListMode,
        val buildings : List<BuildingWithDetails>,
    )

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _listMode, _buildings)
        { t, m, l ->
            when (l) {
                is Result.Success -> Result.Success(content = UIState(t, m, l.content))
                is Result.Error -> Result.Error(t)
                is Result.Loading -> Result.Loading(t)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class OnList(val newValue: ListMode): UIEvent()
        data object OnCreate: UIEvent()
        data class OnEdit(val newValue: BuildingWithDetails): UIEvent()
        data class OnDelete(val newValue: BuildingWithDetails): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnList -> _listMode.value = uiEvent.newValue
            is UIEvent.OnCreate -> navigator.navigate(
                destination = BuildingsDestination.CreateScreen,
            )
            is UIEvent.OnEdit ->
                when(uiEvent.newValue.management.state) {
                    ManagementState.CREATED, ManagementState.BACKUP -> { }
                    ManagementState.HIDDEN ->
                        repository.showBuilding(uiEvent.newValue.building.bid)
                    ManagementState.VALID, ManagementState.INVALID ->
                        navigator.navigate(
                            destination = BuildingsDestination.EditScreen(uiEvent.newValue.building.bid),
                        )
                }
            is UIEvent.OnDelete ->
                when(uiEvent.newValue.management.state) {
                    ManagementState.CREATED, ManagementState.BACKUP, ManagementState.HIDDEN ->
                        repository.deleteBuilding(uiEvent.newValue.building)
                    ManagementState.VALID, ManagementState.INVALID ->
                        repository.hideBuilding(uiEvent.newValue.building.bid)
                }
        }
    }

}