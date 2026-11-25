package fr.uha.hassenforder.easy.stage.validation

import android.net.Uri
import androidx.compose.runtime.Immutable
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails

@Immutable
class BuildingValidationErrors (
    val name : Int?,
    val picture : Int?,
    val chambers : Int?,
) {
    fun hasError () : Boolean {
        if (name != null) return true
        if (picture != null) return true
        if (chambers != null) return true
        return false
    }
}

class BuildingValidator (private val building : BuildingWithDetails, private val chambers : List<ChamberWithDetails>) {

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

    private fun validateChambers(newValue: List<ChamberWithDetails>) : Int? {
        return null
    }

    fun validateAll(): BuildingValidationErrors {
        return BuildingValidationErrors(
            validateName(building.building.name),
            validatePicture(building.building.picture),
            validateChambers(chambers)
        )
    }

}