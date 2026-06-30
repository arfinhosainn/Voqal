package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ImagePicker
import app.voqal.com.navigation.AppNavHost

@Composable
fun App(
    imagePicker: ImagePicker
) {
    VoqalTheme {
        AppNavHost(imagePicker = imagePicker)
    }
}
