package fr.uha.hassenforder.easy.stage.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.stage.database.dao.ChamberDao
import fr.uha.hassenforder.easy.stage.database.dao.SeatDao
import fr.uha.hassenforder.easy.stage.dto.FullChamber
import fr.uha.hassenforder.easy.stage.model.Chamber
import fr.uha.hassenforder.easy.stage.model.Seat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChamberRepository(
    private val dispatcher: CoroutineDispatcher,
    private val chamberDao: ChamberDao,
    private val seatDao: SeatDao,
    private val management: ManagementRepository<Chamber, ManageableDao<Chamber>>,
) {

    fun getById(id: Long): Flow<FullChamber?> {
        return chamberDao.getById(id)
    }

    @WorkerThread
    suspend fun update(content : ChamberDao.ChamberUpdate) = withContext(dispatcher) {
        when (content) {
            is ChamberDao.ChamberUpdate.Name -> chamberDao.update(content)
            is ChamberDao.ChamberUpdate.Picture -> chamberDao.update(content)
            is ChamberDao.ChamberUpdate.NbColumn -> chamberDao.update(content)
            is ChamberDao.ChamberUpdate.NbRow -> chamberDao.update(content)
        }
    }

    @WorkerThread
    suspend fun update(content : SeatDao.SeatUpdate) = withContext(dispatcher) {
        when (content) {
            is SeatDao.SeatUpdate.Type -> seatDao.update(content)
            is SeatDao.SeatUpdate.State -> seatDao.update(content)
        }
    }

    @WorkerThread
    suspend fun createSeats(seats: List<Seat>) {
        seatDao.create(seats)
    }

    @WorkerThread
    suspend fun updateSeats(seats: List<Seat>) {
        seatDao.upsert(seats)
    }

    @WorkerThread
    suspend fun create(chamber: Chamber): Long = management.create(chamber)

    @WorkerThread
    suspend fun valid(rid: Long, valid: Boolean) = management.valid (rid, valid)

    @WorkerThread
    suspend fun delete(chamber: Chamber) = management.delete(chamber.rid, chamber)

    @WorkerThread
    suspend fun edit(rid: Long): Long = management.edit(rid)

    @WorkerThread
    suspend fun restore(rid: Long): Boolean = management.restore(rid)

}
