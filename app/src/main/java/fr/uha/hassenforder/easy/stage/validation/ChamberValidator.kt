package fr.uha.hassenforder.easy.stage.validation

import android.net.Uri
import androidx.compose.runtime.Immutable
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.dto.FullChamber
import fr.uha.hassenforder.easy.stage.model.Seat
import fr.uha.hassenforder.easy.stage.model.SeatType

@Immutable
class ChamberValidationErrors (
    val name : Int?,
    val picture : Int?,
    val nbRow : Int?,
    val nbColumn : Int?,
    val seats : Int?,
) {
    fun hasError () : Boolean {
        if (name != null) return true
        if (picture != null) return true
        if (nbRow != null) return true
        if (nbColumn != null) return true
        if (seats != null) return true
        return false
    }
}

class ChamberValidator (private val chamber: FullChamber) {

    private fun validateName(newValue: String) : Int? {
        return when {
            newValue.isEmpty()  ->  R.string.value_empty
            newValue.isBlank()  ->  R.string.value_blank
            newValue.length < 3 ->  R.string.value_too_short
            newValue.length > 12 ->  R.string.value_too_long
            else -> null
        }
    }

    @Suppress("SameReturnValue")
    private fun validatePicture(newValue: Uri?) : Int? {
        return null
    }

    private fun validateNbRow(newValue: Int) : Int? {
        return when {
            newValue ==  0 ->  R.string.value_zero_forbidden
            newValue <  0 ->  R.string.value_negative_forbidden
            newValue > 31 ->  R.string.value_too_long
            else -> null
        }
    }

    private fun validateNbColumn(newValue: Int) : Int? {
        return when {
            newValue ==  0 ->  R.string.value_zero_forbidden
            newValue <  0 ->  R.string.value_negative_forbidden
            newValue > 31 ->  R.string.value_too_long
            else -> null
        }
    }

    private fun validateSeats(newValue: List<Seat>) : Int? {
        if (newValue.firstOrNull { it.type == SeatType.NO } != null) return R.string.no_seat_type_selected
        return null
    }


    fun validateAll(): ChamberValidationErrors {
        return ChamberValidationErrors(
            validateName(chamber.chamber.name),
            validatePicture(chamber.chamber.picture),
            validateNbRow(chamber.chamber.nbRow),
            validateNbColumn(chamber.chamber.nbColumn),
            validateSeats(chamber.seats),
        )
    }

}