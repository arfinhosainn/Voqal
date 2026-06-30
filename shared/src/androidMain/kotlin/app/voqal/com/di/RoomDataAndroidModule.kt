package app.voqal.com.di


import app.voqal.com.feature.room.StreamRoomCallDataSource
import app.voqal.com.feature.room.StreamVideoConnectionManager
import app.voqal.com.feature.room.data.SupabaseStreamRoomConnectionRepository
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val roomDataAndroidModule = module {
    single {
        StreamVideoConnectionManager(
            context = androidContext(),
            apiKey = "4bwwhuwd4ejv"  // TODO: Replace with your actual Stream API key
        )
    }
    singleOf(::StreamRoomCallDataSource) { bind<RoomCallRemoteDataSource>() }
    singleOf(::SupabaseStreamRoomConnectionRepository) { bind<StreamRoomConnectionRepository>() }
}