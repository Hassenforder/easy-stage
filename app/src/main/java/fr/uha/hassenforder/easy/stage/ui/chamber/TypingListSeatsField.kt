package fr.uha.hassenforder.easy.stage.ui.chamber

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.OutlinedDecorator
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.model.Seat
import fr.uha.hassenforder.easy.stage.model.SeatType
import kotlin.math.abs

data class SeatsProperties (
    val decorator: DecoratorProperties = DecoratorProperties(),
    val width : Int = 0,
    val height : Int = 0,
) {
    fun updateWithErrorId(errorId: Int?): SeatsProperties {
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
    onChange: (seat : Seat, type : SeatType) -> Unit,
    onPropagateSeat: (seat : Seat, direction : ChamberViewModel.Direction) -> Unit,
    width : Int,
    height : Int,
    modifier : Modifier = Modifier,
) {
    val showDialog : MutableState<Seat?> = remember { mutableStateOf(null) }
    val selectedSeat : MutableState<Seat?> = remember { mutableStateOf(null) }

    if (showDialog.value != null) {
        SeatTypePicker (
            onSelect = { it?.let { onChange (showDialog.value!!, it) }; showDialog.value = null },
            titleId = R.string.title_seat_type_select,
        )
    }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

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
            val border = if (selectedSeat.value == item)
                BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                else BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
            Box(
                modifier = Modifier
                    .size((48+1).dp)
                    .clickable(
                        onClick = { showDialog.value = item }
                    )
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress (
                            onDragStart = { selectedSeat.value = item; offsetX = 0f; offsetY = 0f },
                            onDragCancel = { selectedSeat.value = null; offsetX = 0f; offsetY = 0f },
                            onDragEnd = {
                                val direction = when {
                                    abs(offsetY) > abs(offsetX) -> {
                                        if (offsetY >  48) ChamberViewModel.Direction.TO_DOWN
                                        else if (offsetY < -48) ChamberViewModel.Direction.TO_UP
                                        else null
                                    }
                                    else -> {
                                        if (offsetX > 48) ChamberViewModel.Direction.TO_RIGHT
                                        else if (offsetX < -48) ChamberViewModel.Direction.TO_LEFT
                                        else null
                                    }
                                }
                                if (direction != null && selectedSeat.value != null)
                                    onPropagateSeat(selectedSeat.value!!, direction)
                                selectedSeat.value = null
                                offsetX = 0f
                                offsetY = 0f
                            },
                            onDrag = { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        )
                    }
                    .border(border, MaterialTheme.shapes.extraSmall)
                ,
                contentAlignment = Alignment.Center
            ) {
                val type : ImageVector =
                    when (item.type) {
                        SeatType.NO -> Icons.Outlined.DoNotDisturb
                        SeatType.PATH -> Icons.Outlined.Rectangle
                        SeatType.BROKEN -> Icons.Outlined.BrokenImage
                        SeatType.WHEELCHAIR -> Icons.Outlined.WheelchairPickup
                        SeatType.VIP_CHAIR -> Icons.Outlined.Chair
                        SeatType.PREMIUM_CHAIR -> Icons.Outlined.Chair
                        SeatType.REGULAR_CHAIR -> Icons.Outlined.ChairAlt
                    }
                Icon(type, contentDescription = "seat", modifier = Modifier.fillMaxSize())
            }
        }
    }

}

@Composable
private fun InternalEmpty (
) {
    Text (stringResource(id = R.string.empty), color = Color.Red)
}

@Composable
fun TypingListSeatsField(
    values : List<Seat>?,
    onChange: (seat : Seat, type : SeatType) -> Unit,
    onPropagateSeat: (seat : Seat, direction : ChamberViewModel.Direction) -> Unit,
    modifier : Modifier = Modifier,
    config : SeatsProperties = SeatsProperties(),
) {
    OutlinedDecorator(
        modifier = modifier,
        decorator = config.decorator,
    ) {
        if (config.width != 0 && config.height != 0) {
            InternalGrid(values, onChange, onPropagateSeat, config.width, config.height)
        } else {
            InternalEmpty()
        }
    }
}
