package fr.uha.hassenforder.easy.stage.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.stage.database.dao.BuildingDao
import fr.uha.hassenforder.easy.stage.database.dao.BuildingDao.BuildingUpdate
import fr.uha.hassenforder.easy.stage.database.dao.ChamberDao
import fr.uha.hassenforder.easy.stage.dto.BuildingWithDetails
import fr.uha.hassenforder.easy.stage.dto.ChamberWithDetails
import fr.uha.hassenforder.easy.stage.model.Building
import fr.uha.hassenforder.easy.stage.model.Chamber
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BuildingRepository(
    private val dispatcher: CoroutineDispatcher,
    private val buildingDao: BuildingDao,
    private val chamberDao: ChamberDao,
    private val buildingManagement: ManagementRepository<Building, ManageableDao<Building>>,
    private val chamberManagement: ManagementRepository<Chamber, ManageableDao<Chamber>>,
) {

    fun getAllWithDetails(): Flow<List<BuildingWithDetails>> {
        return buildingDao.getAllWithDetails()
    }

    fun getFilteredWithDetails(): Flow<List<BuildingWithDetails>> {
        return buildingDao.getFilteredWithDetails()
    }

    fun getBuildingById(id: Long): Flow<BuildingWithDetails?> {
        return buildingDao.getById(id)
    }

    fun getChambersByBuildingId(buildingId: Long, listMode : ListMode): Flow<List<ChamberWithDetails>> {
        return when(listMode) {
            ListMode.ALL -> chamberDao.getAllWithDetails(buildingId)
            ListMode.FILTERED -> chamberDao.getFilteredWithDetails(buildingId)
        }
    }

    @WorkerThread
    suspend fun update(content : BuildingUpdate) = withContext(dispatcher) {
        when (content) {
            is BuildingUpdate.Name -> buildingDao.update(content)
            is BuildingUpdate.Picture -> buildingDao.update(content)
        }
    }

    @WorkerThread
    suspend fun createBuilding(building: Building): Long = buildingManagement.create(building)

    @WorkerThread
    suspend fun validBuilding(bid: Long, valid: Boolean) = buildingManagement.valid (bid, valid)

    @WorkerThread
    suspend fun deleteBuilding(building: Building) = buildingManagement.delete(building.bid, building)

    @WorkerThread
    suspend fun editBuilding(bid: Long): Long = buildingManagement.edit(bid)

    @WorkerThread
    suspend fun restoreBuilding(bid: Long): Boolean = buildingManagement.restore(bid)

    @WorkerThread
    suspend fun hideBuilding(bid: Long): Boolean = buildingManagement.hide(bid)

    @WorkerThread
    suspend fun showBuilding(bid: Long): Boolean = buildingManagement.show(bid)

    @WorkerThread
    suspend fun deleteChamber(chamber: Chamber) = chamberManagement.delete(chamber.rid, chamber)

    @WorkerThread
    suspend fun hideChamber(rid: Long): Boolean = chamberManagement.hide(rid)

    @WorkerThread
    suspend fun showChamber(rid: Long): Boolean = chamberManagement.show(rid)
}
