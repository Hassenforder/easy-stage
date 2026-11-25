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
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails
import fr.uha.hassenforder.easy.stage.dto.FullChamber
import fr.uha.hassenforder.easy.stage.model.Chamber
import kotlinx.coroutines.flow.Flow

@Dao
interface ChamberDao {

    sealed class ChamberUpdate {
        data class Name(val rid : Long, val name: String) : ChamberUpdate()
        data class Picture(val rid : Long, val picture: Uri?) : ChamberUpdate()
        data class NbRow(val rid : Long, val nbRow: Int) : ChamberUpdate()
        data class NbColumn(val rid : Long, val nbColumn: Int) : ChamberUpdate()
    }

    @Query("SELECT *  FROM chambers")
    fun getAll(): Flow<List<Chamber>>

    @Transaction
    @Query("SELECT R.*, X.* " +
            ", (SELECT COUNT(*) FROM seats S WHERE R.rid = S.rid) AS seatCount " +
            "FROM chambers AS R " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = R.rid AND X.entity = 'Chamber' " +
            "WHERE R.bid = :buildingId " +
            "ORDER BY R.name "
    )
    fun getAllWithDetails(buildingId : Long): Flow<List<ChamberWithDetails>>

    @Transaction
    @Query("SELECT R.*, X.* " +
            ", (SELECT COUNT(*) FROM seats S WHERE R.rid = S.rid) AS seatCount " +
            "FROM chambers AS R " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = R.rid AND X.entity = 'Chamber' AND ( X.state = 'VALID' OR X.state = 'INVALID' ) " +
            "WHERE R.bid = :buildingId " +
            "ORDER BY R.name "
    )
    fun getFilteredWithDetails(buildingId : Long): Flow<List<ChamberWithDetails>>

    @Transaction
    @Query("SELECT R.*, X.* " +
            "FROM chambers AS R " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = R.rid AND X.entity = 'Chamber' " +
            "WHERE R.rid = :roomId "
    )
    fun getById(roomId: Long?): Flow<FullChamber?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(chamber: Chamber): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(chamber: Chamber): Long

    @Delete
    suspend fun delete(chamber: Chamber)

    @Update(entity = Chamber::class)
    suspend fun update(content : ChamberUpdate.Name)

    @Update(entity = Chamber::class)
    suspend fun update(content : ChamberUpdate.Picture)

    @Update(entity = Chamber::class)
    suspend fun update(content : ChamberUpdate.NbRow)

    @Update(entity = Chamber::class)
    suspend fun update(content : ChamberUpdate.NbColumn)

    @Query("SELECT * FROM chambers WHERE rid = :id")
    fun getSyncById(id: Long?): Chamber?

    suspend fun clone (fromId : Long, toId : Long) : Long {
        val chamber : Chamber? = getSyncById(fromId)
        if (chamber == null) return 0
        val cloneId : Long = upsert(chamber.copy(rid=toId))
        return cloneId
    }

    /*
     * Strange but mandatory ...
     * implements the ManageableDao interface in the class create a bug
     * Chamber expect an annotation for all abstract fun even for fun not for it
     * So I chose to implement using a proxy
     */
    fun asManageable () : ManageableDao<Chamber> {
        val self = this
        return object : ManageableDao<Chamber> {
            override suspend fun getSync(id: Long): Chamber? {
                return self.getSyncById(id)
            }
            override suspend fun create(entity: Chamber): Long {
                return self.create(entity)
            }
            override suspend fun delete(entity: Chamber) {
                return self.delete(entity)
            }
            override suspend fun clone(fromId: Long, toId: Long): Long {
                return self.clone(fromId, toId)
            }
            override fun getEntityName(): String {
                return Chamber::class.simpleName!!
            }
        }
    }

}
