package fr.uha.hassenforder.easy.stage.ui.building

import android.net.Uri
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
import fr.uha.hassenforder.easy.stage.database.dao.BuildingDao.BuildingUpdate.*
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails
import fr.uha.hassenforder.easy.stage.model.Building
import fr.uha.hassenforder.easy.stage.navigation.BuildingsDestination
import fr.uha.hassenforder.easy.stage.repository.BuildingRepository
import fr.uha.hassenforder.easy.stage.validation.BuildingValidationErrors
import fr.uha.hassenforder.easy.stage.validation.BuildingValidator
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
class BuildingViewModel @Inject constructor (
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

    private val _listChambersMode = MutableStateFlow(ListMode.FILTERED)

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
    private val _chambers: StateFlow<Result<List<ChamberWithDetails>>> = combine(_id, _listChambersMode)
        { _id, _mode -> _id to _mode }
        .flatMapLatest { repository.getChambersByBuildingId(it.first, it.second) }
        .map { building ->
            Result.Success(content = building)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val listMode: ListMode,
        val building : BuildingWithDetails,
        val chambers : List<ChamberWithDetails>,
        val validation : BuildingValidationErrors,
    ) {
        companion object {
            fun create (uiTitle : UITitle, building : BuildingWithDetails, listMode: ListMode, _chambers : List<ChamberWithDetails>) : UIState {
                val validation = BuildingValidator(building, _chambers).validateAll()
                return UIState(uiTitle, listMode, building, _chambers, validation)
            }
        }

    }

    val uiState : StateFlow<Result<UIState>> = combine (_uiTitle, _building, _listChambersMode, _chambers)
        { t, b, m, c ->
            var reply = Result.notLoadedYet(arrayOf(b, c))
            when (reply) {
                is Result.Error -> Result.Error(reply.uiTitle, reply.exception, reply.message)
                is Result.Loading -> Result.Loading(reply.uiTitle)
                else -> {
                    val b = b as Result.Success<BuildingWithDetails>
                    val c = c as Result.Success<List<ChamberWithDetails>>
                    Result.Success(content = UIState.create(t, b.content, m, c.content))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data class NameChanged(val newValue: String): UIEvent()
        data class PictureChanged(val newValue: Uri?): UIEvent()
        data object Restore: UIEvent()
        data class OnList(val newValue: ListMode): UIEvent()
        data object OnCreateChamber: UIEvent()
        data class OnEditChamber(val newValue: ChamberWithDetails): UIEvent()
        data class OnDeleteChamber(val newValue: ChamberWithDetails): UIEvent()
    }

    private fun validate (oldBuilding : BuildingWithDetails, oldChambers : List<ChamberWithDetails>, uiEvent: UIEvent) : Boolean {
        val newBuilding : BuildingWithDetails = when (uiEvent) {
            is UIEvent.NameChanged -> oldBuilding.copy(building = oldBuilding.building.copy(name = uiEvent.newValue))
            is UIEvent.PictureChanged -> oldBuilding.copy(building = oldBuilding.building.copy(picture = uiEvent.newValue))
            UIEvent.OnBack -> oldBuilding
            UIEvent.Restore -> oldBuilding
            UIEvent.OnCreateChamber -> oldBuilding
            is UIEvent.OnDeleteChamber -> oldBuilding
            is UIEvent.OnEditChamber -> oldBuilding
            is UIEvent.OnList -> oldBuilding
        }
        val newChambers = oldChambers
        val validation = BuildingValidator(newBuilding, newChambers).validateAll()
        val isValid = ! validation.hasError()
        return isValid
    }

    private suspend fun update (building : BuildingWithDetails, uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.NameChanged -> repository.update(Name(bid = building.building.bid, name = uiEvent.newValue))
            is UIEvent.PictureChanged -> repository.update(Picture(bid = building.building.bid, picture = uiEvent.newValue))
            UIEvent.OnBack -> { }
            UIEvent.Restore -> { }
            UIEvent.OnCreateChamber -> { }
            is UIEvent.OnDeleteChamber -> { }
            is UIEvent.OnEditChamber -> { }
            is UIEvent.OnList -> { }
        }
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val building : BuildingWithDetails = (uiState.value as Result.Success<UIState>).content.building
        val chambers : List<ChamberWithDetails> = (uiState.value as Result.Success<UIState>).content.chambers
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.Restore -> repository.restoreBuilding(building.building.bid)
            is UIEvent.OnList -> _listChambersMode.value = uiEvent.newValue
            is UIEvent.OnCreateChamber -> navigator.navigate(
                destination = BuildingsDestination.CreateChamberScreen(building.building.bid),
            )
            is UIEvent.OnEditChamber ->
                when(uiEvent.newValue.management.state) {
                    ManagementState.CREATED, ManagementState.BACKUP -> { }
                    ManagementState.HIDDEN ->
                        repository.showChamber(uiEvent.newValue.chamber.rid)
                    ManagementState.VALID, ManagementState.INVALID ->
                        navigator.navigate(
                            destination = BuildingsDestination.EditChamberScreen(uiEvent.newValue.chamber.rid),
                        )
                }
            is UIEvent.OnDeleteChamber ->
                when(uiEvent.newValue.management.state) {
                    ManagementState.CREATED, ManagementState.BACKUP, ManagementState.HIDDEN ->
                        repository.deleteChamber(uiEvent.newValue.chamber)
                    ManagementState.VALID, ManagementState.INVALID ->
                        repository.hideChamber(uiEvent.newValue.chamber.rid)
                }
            else -> {
                update(building, uiEvent)
                val isValid = validate(building, chambers, uiEvent)
                repository.validBuilding(building.building.bid, isValid)
            }
        }
    }

    fun edit (pid : Long) = viewModelScope.launch {
        val pid : Long = repository.editBuilding(pid)
        _id.value = pid
    }

    fun create(building: Building) = viewModelScope.launch {
        val pid : Long = repository.createBuilding(building)
        _id.value = pid
    }

}
