package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails
import fr.uha.hassenforder.easy.stage.navigation.PerformingDestination
import fr.uha.hassenforder.easy.stage.repository.BuildingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PerformingBuildingViewModel @Inject constructor (
    private val repository: BuildingRepository,
    private val navigator: Navigator
): ViewModel() {

    val uiTitleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = uiTitleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _id: MutableStateFlow<Long> = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _building: StateFlow<Result<BuildingWithDetails>> = _id
        .flatMapLatest { repository.getBuildingById(it) }
        .map { building ->
            if (building != null) {
                uiTitleBuilder.setDocumentName(building.building.name)
                Result.Success(content = building)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _chambers: StateFlow<Result<List<ChamberWithDetails>>> = _id
        .flatMapLatest { repository.getChambersByBuildingId(it, ListMode.FILTERED) }
        .map { building ->
            Result.Success(content = building)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val building : BuildingWithDetails,
        val chambers : List<ChamberWithDetails>,
    )

    val uiState : StateFlow<Result<UIState>> = combine (_uiTitle, _building, _chambers)
        { t, b, c ->
            var reply = Result.notLoadedYet(arrayOf(b, c))
            when (reply) {
                is Result.Error -> Result.Error(reply.uiTitle, reply.exception, reply.message)
                is Result.Loading -> Result.Loading(reply.uiTitle)
                else -> {
                    val b = b as Result.Success<BuildingWithDetails>
                    val c = c as Result.Success<List<ChamberWithDetails>>
                    Result.Success(content = UIState(t, b.content, c.content))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class OnSelectChamber(val newValue: ChamberWithDetails): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnSelectChamber ->
                navigator.navigate(
                    destination = PerformingDestination.PerformingChamberScreen(uiEvent.newValue.chamber.rid),
                )
        }
    }

    fun select (bid : Long) = viewModelScope.launch {
        _id.value = bid
    }

}
