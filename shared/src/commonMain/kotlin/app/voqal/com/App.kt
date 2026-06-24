package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.AddPhotoScreen
import app.voqal.com.presentation.onboarding.ChooseInterestsScreen

@Composable
@Preview
fun App() {
    VoqalTheme {
        ChooseInterestsScreen(onBack = {}, onContinue = {})

    }
}
