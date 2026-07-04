package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ImagePicker
import app.voqal.com.navigation.AppNavHost
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import org.koin.compose.koinInject

@Composable
fun App(
    imagePicker: ImagePicker
) {
    val permissionsController = koinInject<PermissionsController>()
    BindEffect(permissionsController)

    VoqalTheme {
        AppNavHost(imagePicker = imagePicker)
    }
}
