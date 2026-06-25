package app.voqal.com.feature.onboarding.presentation.email

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.BricolageGrotesq
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.feature.onboarding.OnboardingScaffold
import app.voqal.com.feature.onboarding.presentation.components.ValidationHint
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailRoot(
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EmailEvent.NavigateToNext -> onNavigateToNext()
            is EmailEvent.ShowSnackbar -> { /* Hook up snackbar host when available */ }
        }
    }

    EmailScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun EmailScreen(
    state: EmailState,
    onBack: () -> Unit,
    onAction: (EmailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        showTopBar = false,
        currentStep = 1
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What's your email?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = VoqalTheme.colors.onBackground,
                    lineHeight = 36.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We'll send your verification code there",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = VoqalTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            EmailField(
                value = state.email,
                onValueChange = { onAction(EmailAction.OnEmailChange(it)) },
                onDone = {
                    if (state.isFormValid) onAction(EmailAction.OnContinueClick)
                }
            )

            if (state.email.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                ValidationHint(
                    isValid = state.error == null && state.isFormValid,
                    message = state.error
                        ?: if (state.isFormValid) "Email looks good" else "Email is incorrect"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            VoqalPrimaryButton(
                text = "Send code",
                onClick = { onAction(EmailAction.OnContinueClick) },
                enabled = state.isFormValid && !state.isSubmitting,
                loading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "email@example.com",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = BricolageGrotesq,
                color = VoqalTheme.colors.onSurfaceVariant,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(25.dp),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = BricolageGrotesq,
            color = VoqalTheme.colors.onBackground
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = VoqalTheme.colors.surfaceVariant,
            unfocusedContainerColor = VoqalTheme.colors.surfaceVariant,
            disabledContainerColor = VoqalTheme.colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = VoqalTheme.colors.primary,
        ),
    )
}
