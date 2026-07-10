package app.voqal.com.feature.room.di

import app.voqal.com.feature.room.data.SupabaseRoomDiscoveryRepository
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import app.voqal.com.feature.room.domain.RoomRecoveryManager
import app.voqal.com.feature.room.presentation.RoomViewModel
import app.voqal.com.navigation.BottomNavStore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roomPresentationModule = module {
    singleOf(::SupabaseRoomDiscoveryRepository) { bind<RoomDiscoveryRepository>() }
    singleOf(::RoomRecoveryManager)
    single { BottomNavStore() }
    viewModel { RoomViewModel(get(), get(), get()) }
}
