package app.voqal.com.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.presentation.components.BackButton

@Composable
fun OnboardingScaffold(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    currentStep: Int? = null,
    totalSteps: Int = 7,
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoqalTheme.colors.background,
        snackbarHost = snackbarHost,
        topBar = {
            if (showTopBar || currentStep != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = if (showTopBar) 16.dp else 12.dp,
                            bottom = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showTopBar) {
                        BackButton(onClick = onBack)

                        if (currentStep != null) {
                            Spacer(modifier = Modifier.width(14.dp))
                        }
                    }

                    currentStep?.let { step ->
                        LinearProgressIndicator(
                            progress = { step.coerceIn(0, totalSteps).toFloat() / totalSteps },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(100.dp)),
                            color = VoqalTheme.colors.primary,
                            trackColor = VoqalTheme.colors.surfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            content = content
        )
    }
}
