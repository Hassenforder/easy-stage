package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.stage.database.dao.SeatDao.SeatUpdate
import fr.uha.hassenforder.easy.stage.dto.FullChamber
import fr.uha.hassenforder.easy.stage.model.Seat
import fr.uha.hassenforder.easy.stage.model.SeatState
import fr.uha.hassenforder.easy.stage.repository.ChamberRepository
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
class PerformingChamberViewModel @Inject constructor (
    private val repository: ChamberRepository,
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
    private val _chamber: StateFlow<Result<FullChamber>> = _id
        .flatMapLatest { repository.getById(it) }
        .map { chamber ->
            if (chamber != null) {
                uiTitleBuilder.setDocumentName(chamber.chamber.name)
                Result.Success(content = chamber)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val chamber : FullChamber,
    )

    val uiState : StateFlow<Result<UIState>> = combine (_uiTitle, _chamber)
        { t, c ->
            when (c) {
                is Result.Success -> {
                    val missing = hasMissing (c.content)
                    if (! missing) {
                        val sortedSeats = c.content.seats
                            .filter { it.x < c.content.chamber.nbRow && it.y < c.content.chamber.nbColumn }
                            .sortedBy { it.key }
                         Result.Success(content = UIState(t, c.content.copy(seats = sortedSeats)))
                    } else {
                        Result.Error(t)
                    }
                }
                is Result.Error -> Result.Error(t)
                is Result.Loading -> Result.Loading(t)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class ChangeSeatState(val seat : Seat, val state : SeatState): UIEvent()
        data object ClearAllSeats: UIEvent()
        data object FreeAllSeats: UIEvent()
    }

    private suspend fun update (chamber : FullChamber, uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.ChangeSeatState -> repository.update(SeatUpdate.State(uiEvent.seat.sid, uiEvent.state))
            is UIEvent.ClearAllSeats -> repository.updateSeats(buildSeats(chamber, SeatState.NO))
            is UIEvent.FreeAllSeats -> repository.updateSeats(buildSeats(chamber, SeatState.FREE))
            UIEvent.OnBack -> { }
        }
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val chamber : FullChamber = (uiState.value as Result.Success<UIState>).content.chamber
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            else -> {
                update(chamber, uiEvent)
            }
        }
    }

    fun edit (rid : Long) = viewModelScope.launch {
        val rid : Long = repository.edit(rid)
        _id.value = rid
    }

    companion object {
        fun hasMissing(chamber: FullChamber) : Boolean {
            val ordered = chamber.seats.associateBy  { it.key }
            for (x in 0 until chamber.chamber.nbRow) {
                for (y in 0 until chamber.chamber.nbColumn) {
                    if (! ordered.containsKey(Seat.buildKey(x, y))) {
                        return true
                    }
                }
            }
            return false
        }

        fun buildSeats (chamber: FullChamber, state : SeatState) : List<Seat> {
            return chamber.seats.filter { it.type.isAChair() }.map { it.copy(state = state) }
        }

    }

}
