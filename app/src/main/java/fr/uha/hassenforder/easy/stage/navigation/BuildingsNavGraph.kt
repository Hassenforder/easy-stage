package fr.uha.hassenforder.easy.stage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.uha.hassenforder.easy.stage.ui.building.CreateBuildingScreen
import fr.uha.hassenforder.easy.stage.ui.building.EditBuildingScreen
import fr.uha.hassenforder.easy.stage.ui.building.ListBuildingsScreen
import fr.uha.hassenforder.easy.stage.ui.chamber.CreateChamberScreen
import fr.uha.hassenforder.easy.stage.ui.chamber.EditChamberScreen
import kotlinx.serialization.Serializable

sealed interface BuildingsDestination : NavKey {
    @Serializable
    data object ListScreen: NavKey

    @Serializable
    data object CreateScreen: NavKey

    @Serializable
    data class EditScreen(val pid: Long): NavKey

    @Serializable
    data class CreateChamberScreen(val bid: Long): NavKey

    @Serializable
    data class EditChamberScreen(val rid: Long): NavKey
}

fun EntryProviderScope<NavKey>.buildingsNavGraph () {
    entry<BuildingsDestination.ListScreen> {
        ListBuildingsScreen()
    }
    entry<BuildingsDestination.CreateScreen> {
        CreateBuildingScreen()
    }
    entry<BuildingsDestination.EditScreen> {
        EditBuildingScreen(pid = it.pid)
    }
    entry<BuildingsDestination.CreateChamberScreen> {
        CreateChamberScreen(buildingId = it.bid)
    }
    entry<BuildingsDestination.EditChamberScreen> {
        EditChamberScreen(rid = it.rid)
    }
}