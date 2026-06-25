package app.voqal.com.feature.onboarding.presentation.otp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.OnboardingScaffold
import app.voqal.com.feature.onboarding.presentation.components.OtpInputField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OtpRoot(
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OtpViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val focusRequesters = remember { List(6) { FocusRequester() } }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is OtpEvent.NavigateToNext -> onNavigateToNext()
            is OtpEvent.NavigateToEmail -> onBack()
            is OtpEvent.FocusField -> {
                if (event.index in focusRequesters.indices) {
                    focusRequesters[event.index].requestFocus()
                }
            }
            is OtpEvent.ShowSnackbar -> { /* Invoke snackbar display host */ }
        }
    }

    OtpScreen(
        state = state,
        focusRequesters = focusRequesters,
        onAction = viewModel::onAction,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun OtpScreen(
    state: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        currentStep = 2
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // --- Header Title ---
            Text(
                text = "Enter Your OTP Code",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoqalTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // --- Subtitle with Target Phone Mapping ---
            Text(
                text = if (state.emailAddress.isNotBlank()) {
                    "A 6-digit code has been sent to ${state.emailAddress}"
                } else {
                    "A 6-digit code has been sent to your email"
                },
                fontSize = 14.sp,
                color = VoqalTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Change email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VoqalTheme.colors.primary,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onAction(OtpAction.OnChangeEmailClick) }
            )

            Spacer(Modifier.height(40.dp))

            // --- 🔢 6-Digit Entry Row ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.code.forEachIndexed { index, number ->
                    OtpInputField(
                        number = number,
                        focusRequester = focusRequesters[index],
                        onFocusChanged = { isFocused ->
                            if (isFocused) {
                                onAction(OtpAction.OnChangeFieldFocused(index))
                            }
                        },
                        onNumberChanged = { newNumber ->
                            onAction(OtpAction.OnEnterNumber(number = newNumber, index = index))
                        },
                        onKeyboardBack = {
                            onAction(OtpAction.OnKeyboardBack)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (state.code.any { it != null } && !state.isValid) {
                app.voqal.com.feature.onboarding.presentation.components.ValidationHint(
                    isValid = false,
                    message = "Enter the 6-digit code"
                )

                Spacer(Modifier.height(12.dp))
            }

            // --- Help Options Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (state.resendSecondsRemaining > 0) {
                        "Resend in ${state.resendSecondsRemaining}s"
                    } else {
                        "Resend Code"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (state.resendSecondsRemaining > 0) {
                        VoqalTheme.colors.onSurfaceVariant
                    } else {
                        VoqalTheme.colors.primary
                    },
                    modifier = Modifier.clickable(
                        enabled = state.resendSecondsRemaining == 0
                    ) { onAction(OtpAction.OnResendCodeClick) }
                )

                Text(
                    text = "Need Help?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = VoqalTheme.colors.primary,
                    modifier = Modifier.clickable { /* Route directly to standard support help channel */ }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Action Submission ---
            VoqalPrimaryButton(
                text = "Verify",
                onClick = { onAction(OtpAction.OnVerifyClick) },
                enabled = state.isValid && !state.isSubmitting,
                loading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@PreviewLightDark
@Composable
private fun OtpScreenPreview() {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val previewState = OtpState(
        code = listOf(1, 2, 3, null, null, null)
    )

    VoqalTheme {
        OtpScreen(
            state = previewState,
            focusRequesters = focusRequesters,
            onAction = {},
            onBack = {}
        )
    }
}
