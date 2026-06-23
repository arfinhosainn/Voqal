package app.voqal.com

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.AddPhotoScreen
import app.voqal.com.presentation.onboarding.AskFullNameScreen
import app.voqal.com.presentation.onboarding.EnterEmailScreen
import org.jetbrains.compose.resources.painterResource

import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    VoqalTheme {
        var showContent by remember { mutableStateOf(false) }
        EnterEmailScreen(
            onBack = {},
            onContinue = { _, _ -> },
            modifier = Modifier.fillMaxSize()
        )

    }
}
