package fr.uha.hassenforder.easy.stage.ui.chamber

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.stage.R

@Composable
fun SeatingChamberPage(
    uiState: ChamberViewModel.UIState,
    send: (ChamberViewModel.UIEvent) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        TypingListSeatsField(
            uiState.chamber.seats,
            onChange = { seat, type -> send(ChamberViewModel.UIEvent.ChangeSeatType(seat, type)) },
            onPropagateSeat = { seat, direction -> send(ChamberViewModel.UIEvent.PropagateSeat(seat, direction)) },
            modifier = Modifier.fillMaxWidth(),
            config = SeatsProperties (
                decorator = DecoratorProperties(
                    labelId = R.string.seats
                ),
                width = uiState.chamber.chamber.nbRow,
                height = uiState.chamber.chamber.nbColumn,
            )
        )
    }
}
