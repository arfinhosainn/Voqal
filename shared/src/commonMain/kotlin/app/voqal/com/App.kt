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
import app.voqal.com.presentation.onboarding.AskFullNameScreen
import org.jetbrains.compose.resources.painterResource

import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    VoqalTheme {
        var showContent by remember { mutableStateOf(false) }
        AskFullNameScreen(
            onBack = { /* Handle back navigation */ },
            onContinue = { firstName, lastName ->
                // Handle continue action, e.g., save the name and navigate to the next screen
                showContent = true
            },
        )

    }
}
