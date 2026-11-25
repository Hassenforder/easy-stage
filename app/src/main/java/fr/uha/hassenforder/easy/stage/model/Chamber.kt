package fr.uha.hassenforder.easy.stage.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chambers")
@Immutable
data class Chamber(
    @PrimaryKey(autoGenerate = true)
    var rid : Long = 0,
    val bid : Long = 0,
    val name : String = "",
    val picture : Uri? = null,
    val nbRow : Int = 0,
    val nbColumn : Int = 0,
)
