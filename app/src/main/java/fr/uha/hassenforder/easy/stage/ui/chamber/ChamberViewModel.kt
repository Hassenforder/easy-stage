package fr.uha.hassenforder.easy.stage.ui.chamber

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.stage.database.dao.ChamberDao.ChamberUpdate.*
import fr.uha.hassenforder.easy.stage.database.dao.SeatDao.SeatUpdate
import fr.uha.hassenforder.easy.stage.dto.FullChamber
import fr.uha.hassenforder.easy.stage.model.Chamber
import fr.uha.hassenforder.easy.stage.model.Seat
import fr.uha.hassenforder.easy.stage.model.SeatType
import fr.uha.hassenforder.easy.stage.repository.ChamberRepository
import fr.uha.hassenforder.easy.stage.validation.ChamberValidationErrors
import fr.uha.hassenforder.easy.stage.validation.ChamberValidator
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
class ChamberViewModel @Inject constructor (
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
        val validation : ChamberValidationErrors
    ) {
        companion object {
            fun create (uiTitle : UITitle, chamber : FullChamber) : UIState {
                val validation = ChamberValidator(chamber).validateAll()
                return UIState(uiTitle, chamber, validation)
            }
        }

    }

    val uiState : StateFlow<Result<UIState>> = combine (_uiTitle, _chamber)
        { t, c ->
            when (c) {
                is Result.Success -> {
                    val missing = findMissing (c.content)
                    if (missing.isEmpty()) {
                        val sortedSeats = c.content.seats
                            .filter { it.x < c.content.chamber.nbRow && it.y < c.content.chamber.nbColumn }
                            .sortedBy { it.key }
                         Result.Success(content = UIState.create(t, c.content.copy(seats = sortedSeats)))
                    } else {
                        createSeats(missing)
                        Result.Loading()
                    }
                }
                is Result.Error -> Result.Error(t)
                is Result.Loading -> Result.Loading(t)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    enum class Direction {
        TO_RIGHT,
        TO_LEFT,
        TO_UP,
        TO_DOWN,
    }

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class NameChanged(val newValue: String): UIEvent()
        data class PictureChanged(val newValue: Uri?): UIEvent()
        data class NbRowChanged(val newValue: Int): UIEvent()
        data class NbColumnChanged(val newValue: Int): UIEvent()
        data class ChangeSeatType(val seat : Seat, val type : SeatType): UIEvent()
        data class PropagateSeat(val seat : Seat, val direction : Direction): UIEvent()
        data object ClearAllSeats: UIEvent()
        data object Restore: UIEvent()
    }

    private fun replaceSeat(seats: List<Seat>, seat: Seat, type: SeatType): List<Seat> {
        return seats.filter { it.sid != seat.sid } + seat.copy(type = type)
    }

    private fun validate (oldChamber : FullChamber, uiEvent: UIEvent) : Boolean {
        val newChamber : FullChamber = when (uiEvent) {
            is UIEvent.NameChanged -> oldChamber.copy(chamber = oldChamber.chamber.copy(name = uiEvent.newValue))
            is UIEvent.PictureChanged -> oldChamber.copy(chamber = oldChamber.chamber.copy(picture = uiEvent.newValue))
            is UIEvent.NbRowChanged -> oldChamber.copy(chamber = oldChamber.chamber.copy(nbRow = uiEvent.newValue))
            is UIEvent.NbColumnChanged -> oldChamber.copy(chamber = oldChamber.chamber.copy(nbColumn = uiEvent.newValue))
            is UIEvent.ChangeSeatType -> oldChamber.copy(seats = replaceSeat(oldChamber.seats, uiEvent.seat, uiEvent.type))
            is UIEvent.PropagateSeat -> oldChamber
            UIEvent.OnBack -> oldChamber
            UIEvent.Restore -> oldChamber
            UIEvent.ClearAllSeats -> oldChamber
        }
        val validation = ChamberValidator(newChamber).validateAll()
        val isValid = ! validation.hasError()
        return isValid
    }

    private suspend fun update (chamber : FullChamber, uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.NameChanged -> repository.update(Name(rid = chamber.chamber.rid, name = uiEvent.newValue))
            is UIEvent.PictureChanged -> repository.update(Picture(rid = chamber.chamber.rid, picture = uiEvent.newValue))
            is UIEvent.NbRowChanged -> repository.update(NbRow(rid = chamber.chamber.rid, nbRow = uiEvent.newValue))
            is UIEvent.NbColumnChanged -> repository.update(NbColumn(rid = chamber.chamber.rid, nbColumn = uiEvent.newValue))
            is UIEvent.ChangeSeatType -> repository.update(SeatUpdate.Type(uiEvent.seat.sid, uiEvent.type))
            is UIEvent.ClearAllSeats -> repository.updateSeats(buildClearedSeats(chamber))
            is UIEvent.PropagateSeat -> repository.updateSeats(buildPropagateSeats(chamber, uiEvent.seat, uiEvent.direction))
            UIEvent.OnBack -> { }
            UIEvent.Restore -> { }
        }
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val chamber : FullChamber = (uiState.value as Result.Success<UIState>).content.chamber
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.Restore -> repository.restore(chamber.chamber.rid)
            else -> {
                update(chamber, uiEvent)
                val isValid = validate(chamber, uiEvent)
                repository.valid(chamber.chamber.rid, isValid)
            }
        }
    }

    private fun createSeats(seats: List<Seat>) = viewModelScope.launch {
        repository.createSeats(seats)
    }

    fun edit (rid : Long) = viewModelScope.launch {
        val rid : Long = repository.edit(rid)
        _id.value = rid
    }

    fun create(chamber: Chamber) = viewModelScope.launch {
        val pid : Long = repository.create(chamber)
        _id.value = pid
    }

    companion object {
        fun findMissing(chamber: FullChamber) : List<Seat> {
            val ordered = chamber.seats.associateBy  { it.key }
            val missing = mutableListOf<Seat>()
            for (x in 0 until chamber.chamber.nbRow) {
                for (y in 0 until chamber.chamber.nbColumn) {
                    if (! ordered.containsKey(Seat.buildKey(x, y))) {
                        missing.add(Seat(0, chamber.chamber.rid, x, y))
                    }
                }
            }
            return missing
        }

        fun buildClearedSeats (chamber: FullChamber) : List<Seat> {
            return chamber.seats.map { it.copy(type = SeatType.NO) }
        }

        fun buildPropagateSeats(chamber: FullChamber, seat: Seat, direction: Direction): List<Seat> {
            val propagated = mutableListOf<Seat>()
            val type = seat.type
            val ordered = chamber.seats.associateBy  { it.key }
            when (direction) {
                Direction.TO_RIGHT -> {
                    for (x in seat.x+1 until chamber.chamber.nbRow) {
                        val original = ordered[Seat.buildKey(x, seat.y)]
                        if (original != null) propagated.add (original.copy(type = type))
                    }
                }
                Direction.TO_LEFT -> {
                    for (x in 0 until seat.x) {
                        val original = ordered[Seat.buildKey(x, seat.y)]
                        if (original != null) propagated.add (original.copy(type = type))
                    }
                }
                Direction.TO_DOWN ->  {
                    for (y in seat.y+1 until chamber.chamber.nbColumn) {
                        val original = ordered[Seat.buildKey(seat.x, y)]
                        if (original != null) propagated.add (original.copy(type = type))
                    }
                }
                Direction.TO_UP -> {
                    for (y in 0 until seat.y) {
                        val original = ordered[Seat.buildKey(seat.x, y)]
                        if (original != null) propagated.add (original.copy(type = type))
                    }
                }
            }
            return propagated
        }

    }

}
