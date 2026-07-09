package app.voqal.com.core.di

import app.voqal.com.feature.chat.di.chatModule
import app.voqal.com.feature.onboarding.di.onboardingPresentationModule
import app.voqal.com.feature.permission.di.permissionPresentationModule
import app.voqal.com.feature.room.di.roomPresentationModule
import app.voqal.com.feature.rooom_detail.di.roomDetailPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val appModules = listOf(
    permissionModule,
    coreDataModule,
    onboardingPresentationModule,
    permissionPresentationModule,
    chatModule,
    roomPresentationModule,
    roomDetailPresentationModule,
)

expect object KoinInit {
    fun initKoin(config: KoinAppDeclaration? = null)
    fun doInitKoin()
}

fun initKoin(config: KoinAppDeclaration? = null) = KoinInit.initKoin(config)
