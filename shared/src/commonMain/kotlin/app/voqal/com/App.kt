package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameRoot
import app.voqal.com.feature.onboarding.presentation.otp.OtpRoot

@Composable
@Preview
fun App() {
    VoqalTheme {
        OtpRoot(onNavigateToNext ={} , onBack = {})

    }
}
