package app.voqal.com.core.di

import app.voqal.com.core.data.UserPreferencesDataSource
import app.voqal.com.core.data.createUserPreferencesDataStore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreDataModule = module {
    single { createUserPreferencesDataStore() }
    singleOf(::UserPreferencesDataSource)
}
