package fr.uha.hassenforder.easy.stage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.ui.screen.greeting.EasyGreetingScreen
import fr.uha.hassenforder.easy.android.ui.screen.greeting.PortraitGreeting
import fr.uha.hassenforder.easy.stage.R

@Composable
fun GreetingScreen() {
    EasyGreetingScreen (
        titleId = R.string.app_short,
        launch = null,
    ) {
        PortraitGreeting()
    }
}
