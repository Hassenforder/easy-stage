package fr.uha.hassenforder.easy.stage.database.dao

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.model.Building
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {

    sealed class BuildingUpdate {
        data class Name(val bid : Long, val name: String) : BuildingUpdate()
        data class Picture(val bid : Long, val picture: Uri?) : BuildingUpdate()
    }

    @Query("SELECT *  FROM buildings")
    fun getAll(): Flow<List<Building>>

    @Transaction
    @Query("SELECT B.*, X.* " +
            ", (SELECT COUNT(*) FROM chambers R WHERE R.bid = B.bid) AS roomCount " +
            "FROM buildings AS B " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = B.bid AND X.entity = 'Building' " +
            "ORDER BY B.name "
    )
    fun getAllWithDetails(): Flow<List<BuildingWithDetails>>

    @Transaction
    @Query("SELECT B.*, X.* " +
            ", (SELECT COUNT(*) FROM chambers R WHERE R.bid = B.bid) AS roomCount " +
            "FROM buildings AS B " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = B.bid AND X.entity = 'Building' AND ( X.state = 'VALID' OR X.state = 'INVALID' ) " +
            "ORDER BY B.name "
    )
    fun getFilteredWithDetails(): Flow<List<BuildingWithDetails>>

    @Transaction
    @Query("SELECT B.*, X.* " +
            ", (SELECT -1) AS roomCount " +
            "FROM buildings AS B " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = B.bid AND X.entity = 'Building' " +
            "WHERE bid = :buildingId "
    )
    fun getById(buildingId: Long?): Flow<BuildingWithDetails?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(building: Building): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(building: Building): Long

    @Delete
    suspend fun delete(building: Building)

    @Update(entity = Building::class)
    suspend fun update(content : BuildingUpdate.Name)

    @Update(entity = Building::class)
    suspend fun update(content : BuildingUpdate.Picture)

    @Query("SELECT * FROM buildings WHERE bid = :id")
    fun getSyncById(id: Long?): Building?

    suspend fun clone (fromId : Long, toId : Long) : Long {
        val building : Building? = getSyncById(fromId)
        if (building == null) return 0
        val cloneId : Long = upsert(building.copy(bid=toId))
        return cloneId
    }

    /*
     * Strange but mandatory ...
     * implements the ManageableDao interface in the class create a bug
     * Room expect an annotation for all abstract fun even for fun not for it
     * So I chose to implement using a proxy
     */
    fun asManageable () : ManageableDao<Building> {
        val self = this
        return object : ManageableDao<Building> {
            override suspend fun getSync(id: Long): Building? {
                return self.getSyncById(id)
            }
            override suspend fun create(entity: Building): Long {
                return self.create(entity)
            }
            override suspend fun delete(entity: Building) {
                return self.delete(entity)
            }
            override suspend fun clone(fromId: Long, toId: Long): Long {
                return self.clone(fromId, toId)
            }
            override fun getEntityName(): String {
                return Building::class.simpleName!!
            }
        }
    }

}
