package fr.uha.hassenforder.easy.stage.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.stage.model.Building

@Immutable
data class BuildingWithDetails (
    @Embedded
    val building : Building,

    @Embedded
    val management: Management,

    val roomCount : Int,
)