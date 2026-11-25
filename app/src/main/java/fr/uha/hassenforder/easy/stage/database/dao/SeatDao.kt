package fr.uha.hassenforder.easy.stage.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.uha.hassenforder.easy.stage.model.Seat
import fr.uha.hassenforder.easy.stage.model.SeatState
import fr.uha.hassenforder.easy.stage.model.SeatType
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatDao {

    sealed class SeatUpdate {
        data class Type(val sid : Long, val type: SeatType) : SeatUpdate()
        data class State(val sid : Long, val state : SeatState) : SeatUpdate()
    }

    @Transaction
    @Query("SELECT * " +
            "FROM seats AS S " +
            "WHERE rid = :roomId " +
            "ORDER BY x,y "
    )
    fun getAll(roomId : Long): Flow<List<Seat>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(seat: Seat): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(seats: List<Seat>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(seat: Seat): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(seats: List<Seat>)

    @Delete
    suspend fun delete(seat: Seat)

    @Update(entity = Seat::class)
    suspend fun update(content : SeatUpdate.Type)

    @Update(entity = Seat::class)
    suspend fun update(content : SeatUpdate.State)

}
