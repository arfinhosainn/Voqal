package app.voqal.com.core.di

import dev.icerock.moko.permissions.PermissionsController
import org.koin.dsl.module

actual val permissionModule = module {
    single { PermissionsController(applicationContext = get()) }
}
