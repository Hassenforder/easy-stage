package fr.uha.hassenforder.easy.stage.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.stage.model.Chamber

@Immutable
data class ChamberWithDetails (
    @Embedded
    val chamber: Chamber,

    @Embedded
    val management: Management,

    val seatCount : Int,
)