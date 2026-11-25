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
import androidx.compose.material.icons.outlined.Room
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.management.ManagementStateText
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.field.OutlinedDecorator
import fr.uha.hassenforder.easy.android.ui.field.picture.Eraser
import fr.uha.hassenforder.easy.android.ui.field.picture.ExternalCameraApp
import fr.uha.hassenforder.easy.android.ui.field.picture.ExternalCameraAppProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPicker
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.OutlinedPictureField
import fr.uha.hassenforder.easy.android.ui.field.picture.ResourcePicker
import fr.uha.hassenforder.easy.android.ui.field.picture.ResourcePickerProperties
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedTextField
import fr.uha.hassenforder.easy.android.ui.field.text.TextProperties
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.android.ui.field.management.managementListModeMenuEntries
import fr.uha.hassenforder.easy.android.ui.field.management.managementRestoreMenuEntries
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.provider.StageDocumentProvider
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails

@Composable
fun SuccessBuildingScreen(
    uiState: BuildingViewModel.UIState,
    send : (BuildingViewModel.UIEvent) -> Unit,
) {
    val restoreEntries = managementRestoreMenuEntries(
        management = uiState.building.management,
        send = { send(BuildingViewModel.UIEvent.Restore) }
    )
    val modeEntries = managementListModeMenuEntries(
        uiState.listMode,
        { send(BuildingViewModel.UIEvent.OnList(it)) }
    )
    val menuEntries = restoreEntries + modeEntries
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send(BuildingViewModel.UIEvent.OnBack) }, menuEntries) },
        floatingActionButton = {
            FloatingActionButton(onClick = { send(BuildingViewModel.UIEvent.OnCreateChamber) } ) {
                Icon(Icons.Filled.Add, contentDescription = "create")
            }
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            LocalContext.current
            OutlinedTextField(
                value = uiState.building.building.name,
                onValueChange = { send(BuildingViewModel.UIEvent.NameChanged(it)) },
                properties = TextProperties(),
                modifier = Modifier.fillMaxWidth(),
                decorator = DecoratorProperties(
                    labelId = R.string.name,
                    errorId = uiState.validation.name
                ),
            )
            OutlinedPictureField(
                value = uiState.building.building.picture,
                onValueChange = { send(BuildingViewModel.UIEvent.PictureChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                decorator = DecoratorProperties(
                    labelId = R.string.picture,
                    errorId = uiState.validation.picture
                ),
                top = @Composable {
                    Eraser()
                },
                middle = @Composable {
                    GalleryPicker(
                        properties = GalleryPickerProperties(
                            galleryFilter = "image/*",
                        ),
                    )
                    val context = LocalContext.current
                    ResourcePicker(
                        properties = ResourcePickerProperties(
                            uriProvider = { id ->
                                StageDocumentProvider.getResourceDocumentUri(context, id)
                            },
                            from = arrayOf(R.mipmap::class),
                            preFilter = { it.startsWith("building_") },
                        ),
                    )
                },
                bottom = @Composable {
                    val context = LocalContext.current
                    ExternalCameraApp(
                        properties = ExternalCameraAppProperties(
                            uriProvider = {
                                StageDocumentProvider.createExternalImageUri(context)
                            },
                        ),
                    )
                },
            )
            OutlinedDecorator(
                decorator = DecoratorProperties(
                    labelId = R.string.chambers,
                    errorId = uiState.validation.chambers,
                ),
            ) {
                LazyColumn {
                    items(
                        items = uiState.chambers,
                        key = { chamber -> chamber.chamber.rid }
                    ) { item ->
                        SwipeableItem(
                            onEdit = { send(BuildingViewModel.UIEvent.OnEditChamber(item)) },
                            onDelete = { send(BuildingViewModel.UIEvent.OnDeleteChamber(item)) }
                        ) {
                            ChamberItem(item, uiState.listMode)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChamberItem (
    chamber: ChamberWithDetails,
    listMode: ListMode,
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
        supportingContent = {
            Column {
                ManagementStateText(chamber.management.state, listMode)
                Row (
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Outlined.Room, contentDescription = "seat count")
                    Text(chamber.seatCount.toString())
                }
            }
        },
        content = {
            Text(chamber.chamber.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        },
    )
}
