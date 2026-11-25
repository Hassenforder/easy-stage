package fr.uha.hassenforder.easy.stage.ui.chamber

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppMenuEntry
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.field.management.managementRestoreMenuEntries
import fr.uha.hassenforder.easy.android.ui.tab.TabItem
import fr.uha.hassenforder.easy.android.ui.tab.TabLayout
import fr.uha.hassenforder.easy.stage.R

@Composable
fun EditChamberScreen (
    vm: ChamberViewModel = hiltViewModel(),
    rid : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_chambers)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(sid = R.string.title_edit)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.edit(rid)
    }

    StateScreen(state = uiState) {
        SuccessEditScreen(it, { vm.send(it) }, vm)
    }
}

@Composable
private fun SuccessEditScreen(
    uiState: ChamberViewModel.UIState,
    send: (ChamberViewModel.UIEvent) -> Unit,
    vm: ChamberViewModel,
) {
    val restoreEntries = managementRestoreMenuEntries(
        management = uiState.chamber.management,
        send = { send(ChamberViewModel.UIEvent.Restore) }
    )
    val specialsEntries = listOf (
        AppMenuEntry.OverflowIdEntry(
            titleId = R.string.clear,
            listener = { send(ChamberViewModel.UIEvent.ClearAllSeats) }
        ),
    )
    val menuEntries = restoreEntries + specialsEntries
    Scaffold(
        topBar = { AppTopBar( uiState.uiTitle, { send(ChamberViewModel.UIEvent.OnBack) }, menuEntries ) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            TabbedEditChamberScreen(uiState, { vm.send(it) }, vm)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabbedEditChamberScreen (
    uiState: ChamberViewModel.UIState,
    send: (ChamberViewModel.UIEvent) -> Unit,
    vm: ChamberViewModel,
) {
    val tabs = listOf(
        TabItem(
            titleId = R.string.action_structuring,
            icon = Icons.Filled.Construction,
            content = {
                StructuringChamberPage (uiState, { vm.send(it) })
            }
        ),
        TabItem(
            titleId = R.string.action_seat_typing,
            icon = Icons.Filled.AllInclusive,
            content = {
                SeatingChamberPage (uiState, { vm.send(it) })
            }
        ),
    )
    TabLayout(tabs)
}
