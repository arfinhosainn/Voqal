package app.voqal.com.core.di

import app.voqal.com.di.roomDataAndroidModule
import org.koin.dsl.KoinAppDeclaration

/**
 * Android-specific Koin initialization that includes platform-specific modules.
 * This extends the common modules with Android data layer modules.
 */
fun initAndroidKoin(config: KoinAppDeclaration? = null) {
    initKoin {
        // Add Android-specific modules
        modules(roomDataAndroidModule)
        // Apply any additional configuration
        config?.invoke(this)
    }
}

