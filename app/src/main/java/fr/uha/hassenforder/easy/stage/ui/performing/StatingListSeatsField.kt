package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.ChairAlt
import androidx.compose.material.icons.outlined.DoNotDisturb
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.WheelchairPickup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.OutlinedDecorator
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.model.Seat
import fr.uha.hassenforder.easy.stage.model.SeatState
import fr.uha.hassenforder.easy.stage.model.SeatType

data class PerformingSeatsProperties (
    val decorator: DecoratorProperties = DecoratorProperties(),
    val width : Int = 0,
    val height : Int = 0,
) {
    fun updateWithErrorId(errorId: Int?): PerformingSeatsProperties {
        return this.copy (
            decorator = decorator.copy(errorId = errorId)
        )
    }

    fun isError () : Boolean {
        return decorator.isError()
    }

    fun supportingTextId () : Int? {
        return decorator.supportingTextId()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InternalGrid (
    values : List<Seat>?,
    onChange: (seat : Seat, state : SeatState) -> Unit,
    width : Int,
    height : Int,
    modifier : Modifier = Modifier,
) {
    val showDialog : MutableState<Seat?> = remember { mutableStateOf(null) }

    if (showDialog.value != null) {
        SeatStatePicker (
            onSelect = { it?.let { onChange (showDialog.value!!, it) }; showDialog.value = null },
            titleId = R.string.title_seat_state_select,
        )
    }

    LazyVerticalGrid(
        modifier = Modifier.height((height*(49+4)).dp).width((width*(49+4)).dp),
        columns = GridCells.Fixed(width),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        items(
            items = values ?: listOf(),
            key = { it.key }
        ) { item ->
            val border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
            Box(
                modifier = Modifier
                    .size((48+1).dp)
                    .clickable(
                        enabled = item.type.isAChair(),
                        onClick = { showDialog.value = item }
                    )
                    .border(border, MaterialTheme.shapes.extraSmall)
                ,
                contentAlignment = Alignment.Center
            ) {
                Icon(getIcon(item), contentDescription = "seat", modifier = Modifier.fillMaxSize(), tint = getColor(item))
            }
        }
    }

}

private fun getIcon (seat : Seat) : ImageVector {
    return when (seat.type) {
        SeatType.NO -> Icons.Outlined.DoNotDisturb
        SeatType.PATH -> Icons.Outlined.Rectangle
        SeatType.BROKEN -> Icons.Outlined.BrokenImage
        SeatType.WHEELCHAIR -> Icons.Outlined.WheelchairPickup
        SeatType.VIP_CHAIR -> Icons.Outlined.Chair
        SeatType.PREMIUM_CHAIR -> Icons.Outlined.Chair
        SeatType.REGULAR_CHAIR -> Icons.Outlined.ChairAlt
    }
}

private fun getColor (seat : Seat) : Color {
    when (seat.type) {
        SeatType.NO -> return Color.LightGray
        SeatType.PATH -> return Color.LightGray
        SeatType.BROKEN -> return Color.Red
        else -> { }
    }
    return when (seat.state) {
        SeatState.NO -> Color.LightGray
        SeatState.FREE -> Color.Green
        SeatState.USED -> Color.Blue
        SeatState.RESERVED -> Color.Cyan
        SeatState.LOCKED -> Color.Red
        SeatState.MINE -> Color.Magenta
    }
}

@Composable
private fun InternalEmpty (
) {
    Text (stringResource(id = R.string.empty), color = Color.Red)
}

@Composable
fun StatingListSeatsField(
    values : List<Seat>?,
    onChange: (seat : Seat, state : SeatState) -> Unit,
    modifier : Modifier = Modifier,
    config : PerformingSeatsProperties = PerformingSeatsProperties(),
) {
    OutlinedDecorator(
        modifier = modifier,
        decorator = config.decorator,
    ) {
        if (config.width != 0 && config.height != 0) {
            InternalGrid(values, onChange, config.width, config.height)
        } else {
            InternalEmpty()
        }
    }
}
