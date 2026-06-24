package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameRoot
import app.voqal.com.feature.onboarding.presentation.otp.OtpRoot
import app.voqal.com.navigation.AppNavHost

@Composable
@Preview
fun App() {
    VoqalTheme {
        AppNavHost()
    }
}
