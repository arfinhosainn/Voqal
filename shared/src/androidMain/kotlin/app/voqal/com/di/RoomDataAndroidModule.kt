package app.voqal.com.di


import app.voqal.com.feature.room.StreamClientHolder
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
        StreamClientHolder(
            context = androidContext(),
            apiKey = "4bwwhuwd4ejv"
        )
    }
    single {
        StreamVideoConnectionManager(get())
    }
    singleOf(::StreamRoomCallDataSource) { bind<RoomCallRemoteDataSource>() }
    singleOf(::SupabaseStreamRoomConnectionRepository) { bind<StreamRoomConnectionRepository>() }
}