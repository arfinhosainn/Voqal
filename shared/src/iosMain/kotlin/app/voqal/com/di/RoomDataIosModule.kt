package app.voqal.com.di

import app.voqal.com.feature.room.IosStreamRoomConnectionRepository
import app.voqal.com.feature.room.StreamRoomCallDataSource
import app.voqal.com.feature.room.StreamVideoConnectionManager
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import org.koin.dsl.module

val roomDataIosModule = module {
    single {
        StreamVideoConnectionManager(
            apiKey = "4bwwhuwd4ejv"
        )
    }
    single<RoomCallRemoteDataSource> { StreamRoomCallDataSource(get()) }
    single<StreamRoomConnectionRepository> { IosStreamRoomConnectionRepository(get()) }
}
