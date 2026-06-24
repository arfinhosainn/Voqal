package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.AddPhotoScreen

@Composable
@Preview
fun App() {
    VoqalTheme {
        AddPhotoScreen(onBack = {}, onEditClick = {}, onContinue = { _, _ -> } )

    }
}
