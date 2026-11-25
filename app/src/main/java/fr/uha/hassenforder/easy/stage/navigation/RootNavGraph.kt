package fr.uha.hassenforder.easy.stage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.rootNavGraph () {
    greetingNavGraph()
    buildingsNavGraph()
    performingNavGraph()
    settingsNavGraph()
}