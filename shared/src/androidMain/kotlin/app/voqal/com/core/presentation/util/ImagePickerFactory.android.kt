package app.voqal.com.core.presentation.util

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ImagePickerFactory {

    @Composable
    actual fun createPicker(): ImagePicker {
        val activity = LocalActivity.current as ComponentActivity
        return remember(activity) {
            ImagePicker(activity)
        }
    }
}