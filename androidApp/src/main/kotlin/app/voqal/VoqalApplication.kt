package app.voqal


import android.app.Application
import app.voqal.com.core.di.initAndroidKoin
import org.koin.android.ext.koin.androidContext

class VoqalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Starts ONCE and supplies the application context globally
        initAndroidKoin {
            androidContext(this@VoqalApplication)
        }
    }
}