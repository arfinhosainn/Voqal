package app.voqal.com.core.di

import app.voqal.com.feature.onboarding.di.onboardingPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val appModules = listOf(
    onboardingPresentationModule,
)

object KoinInit {
    fun initKoin(config: KoinAppDeclaration? = null) {
        startKoin {
            config?.invoke(this)
            modules(appModules)
        }
    }

    fun initKoin() = initKoin(null)
}

fun initKoin(config: KoinAppDeclaration? = null) = KoinInit.initKoin(config)
