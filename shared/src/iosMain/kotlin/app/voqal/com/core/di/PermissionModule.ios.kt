package app.voqal.com.core.di

import app.voqal.com.core.permissions.data.PermissionManagerImpl
import app.voqal.com.core.permissions.domain.PermissionManager
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsController as ConcretePermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import org.koin.dsl.bind
import org.koin.dsl.module

actual val permissionModule = module {
    single { ConcretePermissionsController() } bind PermissionsControllerProtocol::class bind PermissionsController::class
    single<PermissionManager> { PermissionManagerImpl(get()) }
}
