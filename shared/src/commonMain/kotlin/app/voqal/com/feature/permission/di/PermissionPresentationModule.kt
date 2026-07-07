package app.voqal.com.feature.permission.di

import app.voqal.com.feature.permission.presentation.PermissionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val permissionPresentationModule = module {
    viewModel { params -> 
        PermissionViewModel(
            permissionManager = get(),
            permissionType = params.get(),
            emoji = params.get(),
            title = params.get(),
            description = params.get()
        )
    }
}
