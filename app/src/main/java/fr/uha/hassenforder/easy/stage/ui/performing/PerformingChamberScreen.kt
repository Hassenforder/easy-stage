package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Room
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppMenuEntry
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.stage.R

@Composable
fun PerformingChamberScreen (
    vm: PerformingChamberViewModel = hiltViewModel(),
    rid : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_chambers)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(sid = R.string.title_state)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.edit(rid)
    }

    StateScreen(state = uiState) {
        SuccessEditScreen(it, { vm.send(it) }, vm)
    }
}

@Composable
private fun SuccessEditScreen(
    uiState: PerformingChamberViewModel.UIState,
    send: (PerformingChamberViewModel.UIEvent) -> Unit,
    vm: PerformingChamberViewModel,
) {
    val menuEntries = listOf (
        AppMenuEntry.OverflowIdEntry(
            titleId = R.string.clear,
            listener = { send(PerformingChamberViewModel.UIEvent.ClearAllSeats) }
        ),
        AppMenuEntry.OverflowIdEntry(
            titleId = R.string.free,
            listener = { send(PerformingChamberViewModel.UIEvent.FreeAllSeats) }
        ),
    )
    Scaffold(
        topBar = { AppTopBar( uiState.uiTitle, { send(PerformingChamberViewModel.UIEvent.OnBack) }, menuEntries ) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())
        ) {
            Row (
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                AsyncImage(
                    model = uiState.chamber.chamber.picture,
                    modifier = Modifier.size(96.dp),
                    contentDescription = null,
                    error = rememberVectorPainter(Icons.Outlined.Error),
                    fallback = rememberVectorPainter(Icons.Outlined.Room),
                    placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
                )
                Text(uiState.chamber.chamber.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            StatingListSeatsField(
                uiState.chamber.seats,
                onChange = { seat, state -> send(PerformingChamberViewModel.UIEvent.ChangeSeatState(seat, state)) },
                modifier = Modifier.fillMaxWidth(),
                config = PerformingSeatsProperties (
                    decorator = DecoratorProperties(
                        labelId = R.string.seats
                    ),
                    width = uiState.chamber.chamber.nbRow,
                    height = uiState.chamber.chamber.nbColumn,
                )
            )
        }
    }
}
