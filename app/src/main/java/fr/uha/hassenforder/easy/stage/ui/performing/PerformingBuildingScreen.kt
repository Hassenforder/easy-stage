package fr.uha.hassenforder.easy.stage.ui.performing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Room
import androidx.compose.material.icons.outlined.TheaterComedy
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
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails

@Composable
fun PerformingBuildingScreen (
    vm: PerformingBuildingViewModel = hiltViewModel(),
    bid : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_building)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.select(bid)
    }

    StateScreen(state = uiState) {
        SuccessBuildingScreen(it, { vm.send(it) })
    }
}

@Composable
fun SuccessBuildingScreen(
    uiState: PerformingBuildingViewModel.UIState,
    send : (PerformingBuildingViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(PerformingBuildingViewModel.UIEvent.OnBack) } ) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AsyncImage(
                    model = uiState.building.building.picture,
                    modifier = Modifier.size(96.dp),
                    contentDescription = null,
                    error = rememberVectorPainter(Icons.Outlined.TheaterComedy),
                    fallback = rememberVectorPainter(Icons.Outlined.Room),
                    placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
                )
                Text(uiState.building.building.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            LazyColumn {
                items(
                    items = uiState.chambers,
                    key = { chamber -> chamber.chamber.rid }
                ) { item ->
                    SwipeableItem(
                        onEdit = { send(PerformingBuildingViewModel.UIEvent.OnSelectChamber(item)) },
                    ) {
                        ChamberItem(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChamberItem (
    chamber: ChamberWithDetails,
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            AsyncImage(
                model = chamber.chamber.picture,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                error = rememberVectorPainter(Icons.Outlined.TheaterComedy),
                fallback = rememberVectorPainter(Icons.Outlined.Room),
                placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
            )
        },
        content = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(chamber.chamber.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(chamber.seatCount.toString())
                Text(stringResource(R.string.seats))
            }
        },
    )
}
