package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.navigation.PerformingDestination
import fr.uha.hassenforder.easy.stage.repository.BuildingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformingListBuildingsViewModel @Inject constructor (
    private val repository: BuildingRepository,
    private val navigator: Navigator
): ViewModel() {

    val titleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = titleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _buildings: Flow<Result<List<BuildingWithDetails>>> = repository.getFilteredWithDetails()
        .map { content -> Result.Success(content) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    @Immutable
    data class UIState (
        val uiTitle: UITitle,
        val buildings : List<BuildingWithDetails>,
    )

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _buildings)
        { t, l ->
            when (l) {
                is Result.Success -> Result.Success(content = UIState(t, l.content))
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
        data class OnSelect(val newValue: BuildingWithDetails): UIEvent()
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnSelect ->
                navigator.navigate(
                    destination = PerformingDestination.PerformingBuildingScreen(uiEvent.newValue.building.bid),
                )
        }
    }

}