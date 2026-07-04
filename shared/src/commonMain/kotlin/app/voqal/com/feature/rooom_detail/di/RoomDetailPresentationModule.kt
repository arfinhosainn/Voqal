package app.voqal.com.feature.rooom_detail.di

import app.voqal.com.feature.rooom_detail.presentation.RoomDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roomDetailPresentationModule = module {
    viewModel { RoomDetailViewModel(get(), get(), get(), get(), get()) }
}
