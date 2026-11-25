package fr.uha.hassenforder.easy.stage.ui.chamber

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.model.Chamber

@Composable
fun CreateChamberScreen (
    vm: ChamberViewModel = hiltViewModel(),
    buildingId : Long,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_chamber)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(sid = R.string.title_create)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        val chamber = Chamber (bid = buildingId)
        vm.create(chamber)
    }

    StateScreen(state = uiState) {
        SuccessCreateScreen(it, { vm.send(it) }, vm)
    }

}

@Composable
private fun SuccessCreateScreen(
    uiState: ChamberViewModel.UIState,
    send: (ChamberViewModel.UIEvent) -> Unit,
    vm: ChamberViewModel,
) {
    Scaffold(
        topBar = { AppTopBar( uiState.uiTitle, { send(ChamberViewModel.UIEvent.OnBack) } ) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            StructuringChamberPage (uiState, { vm.send(it) })
        }
    }
}
