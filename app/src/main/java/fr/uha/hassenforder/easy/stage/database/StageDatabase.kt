package fr.uha.hassenforder.easy.stage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uha.hassenforder.easy.android.database.DatabaseTypeConverters
import fr.uha.hassenforder.easy.android.database.dao.ManagementDao
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.stage.database.dao.BuildingDao
import fr.uha.hassenforder.easy.stage.database.dao.ChamberDao
import fr.uha.hassenforder.easy.stage.database.dao.SeatDao
import fr.uha.hassenforder.easy.stage.model.Building
import fr.uha.hassenforder.easy.stage.model.Chamber
import fr.uha.hassenforder.easy.stage.model.Seat

@TypeConverters(DatabaseTypeConverters::class)
@Database(
    entities = [
        Management::class,
        Building::class,
        Chamber::class,
        Seat::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class StageDatabase : RoomDatabase() {

    abstract fun getBuildingDao () : BuildingDao

    abstract fun getChamberDao () : ChamberDao

    abstract fun getSeatDao () : SeatDao

    abstract fun getManagementDao () : ManagementDao

    companion object {
        private lateinit var instance : StageDatabase

        @Synchronized
        fun create (context : Context) : StageDatabase {
            instance = Room.databaseBuilder(context, StageDatabase::class.java, "stage.db").build()
            return instance
        }

        @Synchronized
        fun get () : StageDatabase {
            return instance
        }
    }

}
