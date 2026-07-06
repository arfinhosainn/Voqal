package app.voqal.com.feature.rooom_detail.di

import app.voqal.com.feature.rooom_detail.presentation.RoomDetailViewModel
import app.voqal.com.feature.rooom_detail.presentation.RoomPresentationStore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roomDetailPresentationModule = module {
    single { RoomPresentationStore() }
    viewModel { RoomDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
