package app.voqal.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.OtpScreen
import app.voqal.com.presentation.onboarding.components.OtpState

@Composable
@Preview
fun App() {
    VoqalTheme {
        val focusRequesters = remember {
            List(6) { FocusRequester() }
        }

        val fakeState = OtpState(
            code = List(6) { null },
            isValid = null
        )

        OtpScreen(
            state = fakeState,
            focusRequesters = focusRequesters,
            onAction = {},
            modifier = Modifier
        )

    }
}
