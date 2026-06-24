package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.ChooseLanguageScreen
import app.voqal.com.presentation.onboarding.OtpScreen
import app.voqal.com.presentation.onboarding.components.OtpState

@Composable
@Preview
fun App() {
    VoqalTheme {
        ChooseLanguageScreen(onBack = {}, onContinue = {})

    }
}
