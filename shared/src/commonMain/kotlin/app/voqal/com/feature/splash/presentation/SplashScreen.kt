package app.voqal.com.feature.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit,
    onNotAuthenticated: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SplashEvent.Authenticated -> onAuthenticated()
            SplashEvent.NotAuthenticated -> onNotAuthenticated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoqalTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = VoqalTheme.colors.primary)
    }
}
