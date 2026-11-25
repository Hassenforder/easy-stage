package fr.uha.hassenforder.easy.stage.ui.chamber

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.ChairAlt
import androidx.compose.material.icons.outlined.DoNotDisturb
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.WheelchairPickup
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import fr.uha.hassenforder.easy.android.ui.app.AppTitle
import fr.uha.hassenforder.easy.android.ui.dialog.OutlinedDialog
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.model.SeatType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatTypePicker(
    onSelect: (SeatType?) -> Unit,
    @StringRes titleId: Int = R.string.title_seat_type_select,
) {
    val seatTypes = remember { SeatType.entries }

    OutlinedDialog(onDismiss = { onSelect(null) }, titleId) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ){
            items(
                items = seatTypes,
            ) { item : SeatType ->
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
                val type : ImageVector =
                    when (item) {
                        SeatType.NO -> Icons.Outlined.DoNotDisturb
                        SeatType.PATH -> Icons.Outlined.Rectangle
                        SeatType.BROKEN -> Icons.Outlined.BrokenImage
                        SeatType.WHEELCHAIR -> Icons.Outlined.WheelchairPickup
                        SeatType.VIP_CHAIR -> Icons.Outlined.Chair
                        SeatType.PREMIUM_CHAIR -> Icons.Outlined.Chair
                        SeatType.REGULAR_CHAIR -> Icons.Outlined.ChairAlt
                    }
                ListItem(
                    modifier = Modifier.clickable(onClick = { onSelect(item) }),
                    leadingContent = {
                        Icon(type, contentDescription = "seat")
                    },
                    content = { Text(item.toString()) },
                )
            }
        }
    }

}
