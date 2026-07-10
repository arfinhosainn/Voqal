package app.voqal.com.core.di

import app.voqal.com.di.roomDataIosModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

actual object KoinInit {
    actual fun initKoin(config: KoinAppDeclaration?) {
        val koin = startKoin {
            config?.invoke(this)
            modules(appModules + roomDataIosModule)
        }.koin
        runRoomRecovery(koin)
    }

    actual fun doInitKoin() {
        initKoin(null)
    }
}
