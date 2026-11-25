package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import fr.uha.hassenforder.easy.android.ui.app.AppTitle
import fr.uha.hassenforder.easy.android.ui.dialog.OutlinedDialog
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.model.SeatState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatStatePicker(
    onSelect: (SeatState?) -> Unit,
    titleId: Int = R.string.title_seat_state_select,
) {
    val seatStates = remember { SeatState.entries }

    OutlinedDialog(onDismiss = { onSelect(null) }, titleId) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ){
            items(
                items = seatStates,
            ) { item : SeatState ->
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
                val color : Color =
                    when (item) {
                        SeatState.NO -> Color.LightGray
                        SeatState.FREE -> Color.Green
                        SeatState.USED -> Color.Blue
                        SeatState.RESERVED -> Color.Cyan
                        SeatState.LOCKED -> Color.Red
                        SeatState.MINE -> Color.Magenta
                    }
                ListItem(
                    modifier = Modifier.clickable(onClick = { onSelect(item) }),
                    leadingContent = {
                        Icon(Icons.Outlined.Chair, contentDescription = "seat", tint = color)
                    },
                    content = { Text(item.toString()) },
                )
            }
        }
    }

}
