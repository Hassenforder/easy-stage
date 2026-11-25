package fr.uha.hassenforder.easy.stage.database

import android.content.Context
import android.net.Uri
import fr.uha.hassenforder.easy.android.repository.ManagementHelper
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.stage.R
import fr.uha.hassenforder.easy.stage.provider.StageDocumentProvider
import fr.uha.hassenforder.easy.stage.database.dao.BuildingDao
import fr.uha.hassenforder.easy.stage.database.dao.ChamberDao
import fr.uha.hassenforder.easy.stage.database.dao.SeatDao
import fr.uha.hassenforder.easy.stage.model.*
import kotlinx.coroutines.Dispatchers

class FeedDatabase (
    private val context: Context,
    private val db : StageDatabase,
) {
    private fun buildResourceUri (id : Int) : Uri {
        return StageDocumentProvider.getResourceDocumentUri(context, id)
    }

    private suspend fun feedEnsisaBuilding(): Building {
        val dao: BuildingDao = db.getBuildingDao()
        val building = Building(0, "Ensisa", buildResourceUri(R.mipmap.building_ensisa))
        val management = ManagementRepository(Dispatchers.IO, db.getManagementDao(), dao.asManageable(), ManagementHelper(db.getManagementDao()))
        val id = management.create(building)
        building.bid = id
        management.valid(id, true)
        return building
    }

    private suspend fun feedBuildings(): Array<Building> {
        val dao: BuildingDao = db.getBuildingDao()
        val buildings = arrayOf (
            Building(0, "Ensisa", buildResourceUri(R.mipmap.building_ensisa)),
            Building(0, "MacDo", buildResourceUri(R.mipmap.building_macdo)),
            Building(0, "Theater", buildResourceUri(R.mipmap.building_theatre)),
        )
        val management = ManagementRepository(Dispatchers.IO, db.getManagementDao(), dao.asManageable(), ManagementHelper(db.getManagementDao()))
        buildings.forEach {
            val id = management.create(it)
            it.bid = id
            management.valid(id, true)
        }
        return buildings
    }

    private suspend fun feedEnsisaRooms(ensisa : Building) : Array<Chamber> {
        val dao: ChamberDao = db.getChamberDao()
        val chambers = arrayOf (
            Chamber(0, ensisa.bid, "E25", buildResourceUri(R.mipmap.room_e25), 7, 10),
            Chamber(0, ensisa.bid, "GA", buildResourceUri(R.mipmap.room_ga), 10, 21),
            Chamber(0, ensisa.bid, "PA", buildResourceUri(R.mipmap.room_pa), 8, 13),
            Chamber(0, ensisa.bid, "E37", buildResourceUri(R.mipmap.room_e37), 3, 12),
        )
        val management = ManagementRepository(Dispatchers.IO, db.getManagementDao(), dao.asManageable(), ManagementHelper(db.getManagementDao()))
        chambers.forEach {
            val id = management.create(it)
            it.rid = id
            management.valid(id, true)
        }
        return chambers
    }

    private suspend fun fillColumnWithSeats (chamber : Chamber, column : Int, from : Int, to : Int, type : SeatType) {
        val dao: SeatDao = db.getSeatDao()
        val seats = (from until to).map {
            Seat(0, chamber.rid, it, column, type)
        }
        dao.create(seats)
    }

    private suspend fun feedE25Seats(chamber : Chamber) {
        fillColumnWithSeats(chamber, 0, 0, chamber.nbRow, SeatType.PATH)
        fillColumnWithSeats(chamber, 1, 0, chamber.nbRow, SeatType.PATH)
        fillColumnWithSeats(chamber, 2, 0, chamber.nbRow, SeatType.WHEELCHAIR)
        (3 until chamber.nbColumn).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow, SeatType.REGULAR_CHAIR)
        }
    }

    private suspend fun feedGASeats(chamber : Chamber) {
        (0 until 5).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow-1, SeatType.REGULAR_CHAIR)
        }
        (0 until 5).forEach {
            fillColumnWithSeats(chamber, it, chamber.nbRow, chamber.nbRow, SeatType.PATH)
        }
        fillColumnWithSeats(chamber, 5, 0, chamber.nbRow, SeatType.PATH)
        (6 until 9).forEach {
            fillColumnWithSeats(chamber, it, 1, chamber.nbRow, SeatType.REGULAR_CHAIR)
        }
        (9 until 12).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow, SeatType.REGULAR_CHAIR)
        }
        (12 until chamber.nbColumn).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow, SeatType.PATH)
        }
    }

    private suspend fun feedPASeats(chamber : Chamber) {
        fillColumnWithSeats(chamber, 0, 0, chamber.nbRow, SeatType.PATH)
        (1 until chamber.nbColumn-1).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow, SeatType.REGULAR_CHAIR)
        }
        fillColumnWithSeats(chamber, chamber.nbColumn-1, 0, chamber.nbRow, SeatType.PATH)
        (1 until chamber.nbColumn-1).forEach {
            val type = if (it < 5 || it > chamber.nbColumn-6) SeatType.WHEELCHAIR else SeatType.REGULAR_CHAIR
            fillColumnWithSeats(chamber, it, 0, 0, type)
        }
    }

    private suspend fun feedE37Seats(chamber : Chamber) {
        (0 until 4).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow, SeatType.REGULAR_CHAIR)
        }
        fillColumnWithSeats(chamber, 4, 0, chamber.nbRow, SeatType.WHEELCHAIR)
        fillColumnWithSeats(chamber, 5, 0, chamber.nbRow, SeatType.PATH)
        fillColumnWithSeats(chamber, 6, 0, chamber.nbRow, SeatType.PATH)
        fillColumnWithSeats(chamber, 7, 0, chamber.nbRow, SeatType.WHEELCHAIR)
        (8 until 12).forEach {
            fillColumnWithSeats(chamber, it, 0, chamber.nbRow, SeatType.REGULAR_CHAIR)
        }
    }

    private suspend fun feedEnsisaRoomsSeats(chambers : Array<Chamber>) {
        chambers.forEach {
            if (it.name == "E25") feedE25Seats(it)
            if (it.name == "GA") feedGASeats(it)
            if (it.name == "PA") feedPASeats(it)
            if (it.name == "E37") feedE37Seats(it)
        }
    }

    suspend fun populate(mode : Int) {
        when (mode) {
            1 -> {
                val buildings = feedBuildings()
                val ensisaRooms = feedEnsisaRooms(buildings[0])
                feedEnsisaRoomsSeats(ensisaRooms)
            }
            2 -> {
                val building = feedEnsisaBuilding()
                val ensisaRooms = feedEnsisaRooms(building)
                feedEnsisaRoomsSeats(ensisaRooms)
            }
        }
    }

    fun clear() {
        db.clearAllTables()
    }

}