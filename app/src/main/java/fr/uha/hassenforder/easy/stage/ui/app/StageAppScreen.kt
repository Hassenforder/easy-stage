package fr.uha.hassenforder.easy.stage.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Attractions
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.uha.hassenforder.easy.android.model.FlexibleIcon
import fr.uha.hassenforder.easy.android.model.IconRender
import fr.uha.hassenforder.easy.android.ui.app.AppNavigatorScreen
import fr.uha.hassenforder.easy.android.ui.app.PrimaryNavigation
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.navigation.BuildingsDestination
import fr.uha.hassenforder.easy.stage.navigation.GreetingDestination
import fr.uha.hassenforder.easy.stage.navigation.PerformingDestination
import fr.uha.hassenforder.easy.stage.navigation.SettingsDestination

@Composable
fun StageAppScreen(
    vm: AppViewModel = hiltViewModel(),
) {
    AppNavigatorScreen(vm.navigator, primaryNavigations)
}

private val primaryNavigations = listOf (
    PrimaryNavigation(
        destination = GreetingDestination.GreetingScreen,
        icon = IconRender(
            focused = FlexibleIcon(vector = Icons.Filled.Home),
            unfocused = FlexibleIcon(vector = Icons.Outlined.Home)
        ),
        labelId = R.string.app_name
    ),
    PrimaryNavigation(
        destination = BuildingsDestination.ListScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.House), unfocused = FlexibleIcon(vector= Icons.Outlined.House)),
        labelId = R.string.action_buildings
    ),
    PrimaryNavigation(
        destination = PerformingDestination.PerformingListScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Attractions), unfocused = FlexibleIcon(vector= Icons.Outlined.Attractions)),
        labelId = R.string.action_performing
    ),
    PrimaryNavigation(
        destination = SettingsDestination.SettingsScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Settings), unfocused = FlexibleIcon(vector= Icons.Outlined.Settings)),
        labelId = R.string.action_settings
    ),
)
