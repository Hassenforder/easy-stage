package fr.uha.hassenforder.easy.stage.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.stage.model.Chamber
import fr.uha.hassenforder.easy.stage.model.Seat

@Immutable
data class FullChamber (
    @Embedded
    val chamber: Chamber,

    @Embedded
    val management: Management,

    @Relation(parentColumn = "rid", entityColumn = "rid")
    val seats: List<Seat>,
)