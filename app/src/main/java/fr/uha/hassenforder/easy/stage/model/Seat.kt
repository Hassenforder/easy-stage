package fr.uha.hassenforder.easy.stage.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "seats")
@Immutable
data class Seat(
    @PrimaryKey(autoGenerate = true)
    val sid : Long = 0,
    val rid : Long = 0,
    val x : Int = 0,
    val y : Int = 0,
    val type : SeatType = SeatType.NO,
    val state : SeatState = SeatState.NO,
) {
    @Ignore
    val key = buildKey(x, y)

    companion object {
        fun buildKey (x :Int, y:Int) : Int {
            return 1000 * y + x
        }
    }

}
