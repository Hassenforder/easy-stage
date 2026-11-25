package fr.uha.hassenforder.easy.stage.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.screen.MetaDataScreen
import fr.uha.hassenforder.easy.stage.R

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = { AppTopBar(UITitle(screenNameId = R.string.title_settings)) },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Button(onClick = { vm.populateDatabase(1) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.populate))
            }
            Button(onClick = { vm.populateDatabase(2) }, modifier = Modifier.fillMaxWidth()) {
                Text("Ensisa only")
            }
            Button(onClick = { vm.clearDatabase() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.clear))
            }
            MetaDataScreen(initiallyOpened = false, modifier = Modifier.fillMaxSize())
        }
    }

}