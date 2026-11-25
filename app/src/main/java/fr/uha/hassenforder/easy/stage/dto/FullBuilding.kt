package fr.uha.hassenforder.easy.stage.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.stage.model.Building

@Immutable
data class FullBuilding (
    @Embedded
    val building: Building,

    @Embedded
    val management: Management,

    @Relation(parentColumn = "bid", entityColumn = "bid")
    val chambers: List<ChamberWithDetails>,
)