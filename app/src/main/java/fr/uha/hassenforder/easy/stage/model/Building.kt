package fr.uha.hassenforder.easy.stage.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buildings")
@Immutable
data class Building(
    @PrimaryKey(autoGenerate = true)
    var bid : Long = 0,
    val name : String = "",
    val picture : Uri? = null,
)
