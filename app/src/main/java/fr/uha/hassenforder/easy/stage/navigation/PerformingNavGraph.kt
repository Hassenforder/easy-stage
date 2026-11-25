package fr.uha.hassenforder.easy.stage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.uha.hassenforder.easy.stage.ui.performing.PerformingBuildingScreen
import fr.uha.hassenforder.easy.stage.ui.performing.PerformingChamberScreen
import fr.uha.hassenforder.easy.stage.ui.performing.PerformingListBuildingsScreen
import kotlinx.serialization.Serializable

sealed interface PerformingDestination : NavKey {
    @Serializable
    data object PerformingListScreen: NavKey

    @Serializable
    data class PerformingBuildingScreen(val bid: Long): NavKey

    @Serializable
    data class PerformingChamberScreen(val rid: Long): NavKey

}

fun EntryProviderScope<NavKey>.performingNavGraph () {
    entry<PerformingDestination.PerformingListScreen> {
        PerformingListBuildingsScreen()
    }
    entry<PerformingDestination.PerformingBuildingScreen> {
        PerformingBuildingScreen(bid = it.bid)
    }
    entry<PerformingDestination.PerformingChamberScreen> {
        PerformingChamberScreen(rid = it.rid)
    }
}