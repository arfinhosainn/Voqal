package app.voqal.com.core.di

import app.voqal.com.core.permissions.data.PermissionManagerImpl
import app.voqal.com.core.permissions.domain.PermissionManager
import dev.icerock.moko.permissions.PermissionsController
import org.koin.dsl.module

actual val permissionModule = module {
    single<PermissionsController> { PermissionsController(applicationContext = get()) }
    single<PermissionManager> { PermissionManagerImpl(get()) }
}
