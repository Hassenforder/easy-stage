package fr.uha.hassenforder.easy.stage.ui.building

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Room
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.ui.field.management.ManagementStateText
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.android.ui.field.management.managementListModeMenuEntries
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.ui.building.ListBuildingsViewModel.UIState

@Composable
fun ListBuildingsScreen(
    vm: ListBuildingsViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_buildings)

    LaunchedEffect(Unit) {
        vm.titleBuilder.setScreenNameId(sid = R.string.title_list)
        vm.titleBuilder.setDefaultDocumentName(defaultDocumentName)
    }

    StateScreen(uiState) {
        SuccessListScreen(it, { vm.send(it) } )
    }
}

@Composable
private fun SuccessListScreen(
    uiState: UIState,
    send : (ListBuildingsViewModel.UIEvent) -> Unit,
) {
    val menuEntries = managementListModeMenuEntries(
        uiState.listMode,
        { send(ListBuildingsViewModel.UIEvent.OnList(it)) }
    )
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(ListBuildingsViewModel.UIEvent.OnBack) }, menuEntries ) },
        floatingActionButton = {
            FloatingActionButton(onClick = { send(ListBuildingsViewModel.UIEvent.OnCreate) } ) {
                Icon(Icons.Filled.Add, contentDescription = "create")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                items = uiState.buildings,
                key = { building -> building.building.bid }
            ) { item ->
                SwipeableItem(
                    onEdit = { send(ListBuildingsViewModel.UIEvent.OnEdit(item)) },
                    onDelete = { send(ListBuildingsViewModel.UIEvent.OnDelete(item)) }
                ) {
                    BuildingItem(item, uiState.listMode)
                }
            }
        }
    }
}

@Composable
private fun BuildingItem (
    building : BuildingWithDetails,
    listMode : ListMode,
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
        supportingContent = {
            Column {
                ManagementStateText(building.management.state, listMode)
                Row (
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Outlined.Room, contentDescription = "room count")
                    Text(building.roomCount.toString())
                }
            }
        },
        content = {
            Text(building.building.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        },
    )
}
