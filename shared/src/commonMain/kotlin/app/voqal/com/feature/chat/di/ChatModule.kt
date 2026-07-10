package app.voqal.com.feature.chat.di

import app.voqal.com.feature.chat.data.SupabaseChatRepository
import app.voqal.com.feature.chat.data.datasource.ChatRemoteDataSource
import app.voqal.com.feature.chat.data.datasource.SupabaseChatRemoteDataSource
import app.voqal.com.feature.chat.domain.repository.ChatRepository
import app.voqal.com.feature.chat.domain.usecase.LoadMoreMessagesUseCase
import app.voqal.com.feature.chat.domain.usecase.ObserveMessagesUseCase
import app.voqal.com.feature.chat.domain.usecase.SendMessageUseCase
import app.voqal.com.feature.chat.presentation.ChatViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val chatModule = module {
    singleOf(::SupabaseChatRemoteDataSource) { bind<ChatRemoteDataSource>() }
    singleOf(::SupabaseChatRepository) { bind<ChatRepository>() }
    
    singleOf(::ObserveMessagesUseCase)
    singleOf(::SendMessageUseCase)
    singleOf(::LoadMoreMessagesUseCase)
    
    factory { (roomId: String) -> ChatViewModel(
        roomId = roomId,
        observeMessagesUseCase = get(),
        sendMessageUseCase = get(),
        connectionRepository = get()
    ) }
}
