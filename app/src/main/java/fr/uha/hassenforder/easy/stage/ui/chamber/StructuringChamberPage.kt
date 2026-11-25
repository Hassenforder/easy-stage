package fr.uha.hassenforder.easy.stage.ui.chamber

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.Eraser
import fr.uha.hassenforder.easy.android.ui.field.picture.ExternalCameraApp
import fr.uha.hassenforder.easy.android.ui.field.picture.ExternalCameraAppProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPicker
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.OutlinedPictureField
import fr.uha.hassenforder.easy.android.ui.field.picture.ResourcePicker
import fr.uha.hassenforder.easy.android.ui.field.picture.ResourcePickerProperties
import fr.uha.hassenforder.easy.android.ui.field.text.ActionOneUnitLess
import fr.uha.hassenforder.easy.android.ui.field.text.ActionOneUnitMore
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedIntField
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedTextField
import fr.uha.hassenforder.easy.android.ui.field.text.TextProperties
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.provider.StageDocumentProvider

@Composable
fun StructuringChamberPage(
    uiState: ChamberViewModel.UIState,
    send: (ChamberViewModel.UIEvent) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = uiState.chamber.chamber.name,
            properties = TextProperties(),
            onValueChange = { send(ChamberViewModel.UIEvent.NameChanged(it)) },
            decorator = DecoratorProperties(
                labelId = R.string.name,
                errorId = uiState.validation.name
            ),
        )
        OutlinedPictureField(
            value = uiState.chamber.chamber.picture,
            onValueChange = { send(ChamberViewModel.UIEvent.PictureChanged(it)) },
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
                        preFilter = { it.startsWith("room_") },
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
        OutlinedIntField(
            value = uiState.chamber.chamber.nbRow,
            onValueChange = { send(ChamberViewModel.UIEvent.NbRowChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.nb_row,
                errorId = uiState.validation.nbRow
            ),
            trailing = {
                ActionOneUnitLess()
                ActionOneUnitMore()
            },
        )
        OutlinedIntField(
            value = uiState.chamber.chamber.nbColumn,
            onValueChange = { send(ChamberViewModel.UIEvent.NbColumnChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.nb_column,
                errorId = uiState.validation.nbColumn
            ),
            trailing = {
                ActionOneUnitLess()
                ActionOneUnitMore()
            },
        )
    }
}
