package fr.uha.hassenforder.easy.stage.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.stage.database.FeedDatabase
import fr.uha.hassenforder.easy.stage.database.StageDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val context: Context,
    private val db: StageDatabase,
): ViewModel() {

    fun populateDatabase (mode : Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            FeedDatabase(context, db).populate(mode)
        }
    }

    fun clearDatabase () = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            FeedDatabase(context, db).clear()
        }
    }

}