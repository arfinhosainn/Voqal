package app.voqal.com.core.data

import org.koin.mp.KoinPlatform.getKoin

actual fun getDataStorePath(fileName: String): String {
    val context = getKoin().get<android.content.Context>()
    return context.filesDir.resolve(fileName).absolutePath
}
