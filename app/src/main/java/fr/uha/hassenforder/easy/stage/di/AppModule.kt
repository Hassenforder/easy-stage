package fr.uha.hassenforder.easy.stage.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.android.database.dao.ManagementDao
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.navigation.NavigatorType
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.stage.database.StageDatabase
import fr.uha.hassenforder.easy.stage.database.dao.BuildingDao
import fr.uha.hassenforder.easy.stage.database.dao.ChamberDao
import fr.uha.hassenforder.easy.stage.database.dao.SeatDao
import fr.uha.hassenforder.easy.stage.model.Building
import fr.uha.hassenforder.easy.stage.model.Chamber
import fr.uha.hassenforder.easy.stage.navigation.GreetingDestination
import fr.uha.hassenforder.easy.stage.navigation.rootNavGraph
import fr.uha.hassenforder.easy.stage.repository.BuildingRepository
import fr.uha.hassenforder.easy.stage.repository.ChamberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext appContext: Context) = appContext

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = StageDatabase.create(appContext)

    @Singleton
    @Provides
    fun provideBuildingDao(db: StageDatabase) = db.getBuildingDao()

    @Singleton
    @Provides
    fun provideChamberDao(db: StageDatabase) = db.getChamberDao()

    @Singleton
    @Provides
    fun provideSeatDao(db: StageDatabase) = db.getSeatDao()

    @Singleton
    @Provides
    fun provideManagementDao(db: StageDatabase) = db.getManagementDao()

    @Singleton
    @Provides
    fun provideNavigator() : Navigator = Navigator(
        startDestination = GreetingDestination.GreetingScreen,
        type = NavigatorType.SINGLETOP,
        navigationGraph = { rootNavGraph() }
    )

    @Singleton
    @Provides
    fun provideManagementBuildingRepository(
        dispatcher: CoroutineDispatcher,
        buildingDao: BuildingDao,
        managementDao: ManagementDao,
    ) = ManagementRepository(dispatcher, managementDao, buildingDao.asManageable())

    @Singleton
    @Provides
    fun provideManagementChamberRepository(
        dispatcher: CoroutineDispatcher,
        chamberDao : ChamberDao,
        managementDao: ManagementDao,
    ) = ManagementRepository(dispatcher, managementDao, chamberDao.asManageable())

    @Singleton
    @Provides
    fun provideBuildingRepository(
        ioDispatcher: CoroutineDispatcher,
        buildingDao: BuildingDao,
        chamberDao: ChamberDao,
        buildingManagement: ManagementRepository<Building, ManageableDao<Building>>,
        chamberManagement: ManagementRepository<Chamber, ManageableDao<Chamber>>,
    ) = BuildingRepository(ioDispatcher, buildingDao, chamberDao, buildingManagement, chamberManagement)

    @Singleton
    @Provides
    fun provideChamberRepository(
        ioDispatcher: CoroutineDispatcher,
        chamberDao : ChamberDao,
        seatDao : SeatDao,
        management: ManagementRepository<Chamber, ManageableDao<Chamber>>,
    ) = ChamberRepository(ioDispatcher, chamberDao, seatDao, management)

}