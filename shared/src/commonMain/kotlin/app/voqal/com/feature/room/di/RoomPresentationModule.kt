package app.voqal.com.feature.room.di

import app.voqal.com.feature.room.presentation.RoomViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val roomPresentationModule = module {
    viewModelOf(::RoomViewModel)
}
