package fr.uha.hassenforder.easy.stage.ui.building

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.model.Building

@Composable
fun CreateBuildingScreen (
    vm: BuildingViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_building)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(sid = R.string.title_create)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        val building = Building ()
        vm.create(building)
    }

    StateScreen(state = uiState) {
        SuccessBuildingScreen(it, { vm.send(it) })
    }

}
