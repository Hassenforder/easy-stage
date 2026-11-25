package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.ui.performing.PerformingListBuildingsViewModel.UIState

@Composable
fun PerformingListBuildingsScreen(
    vm: PerformingListBuildingsViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_buildings)

    LaunchedEffect(Unit) {
        vm.titleBuilder.setDefaultDocumentName(defaultDocumentName)
    }

    StateScreen(uiState) {
        SuccessListScreen(it, { vm.send(it) } )
    }
}

@Composable
private fun SuccessListScreen(
    uiState: UIState,
    send : (PerformingListBuildingsViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(PerformingListBuildingsViewModel.UIEvent.OnBack) } ) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                items = uiState.buildings,
                key = { building -> building.building.bid }
            ) { item ->
                SwipeableItem(
                    onEdit = { send(PerformingListBuildingsViewModel.UIEvent.OnSelect(item)) },
                ) {
                    BuildingItem(item)
                }
            }
        }
    }
}

@Composable
private fun BuildingItem (
    building : BuildingWithDetails,
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            AsyncImage(
                model = building.building.picture,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                error = rememberVectorPainter(Icons.Outlined.TheaterComedy),
                fallback = rememberVectorPainter(Icons.Outlined.House),
                placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
            )
        },
        content = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(building.building.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(building.roomCount.toString())
                Text(stringResource(R.string.chambers))
            }
        },
    )
}
