package app.voqal.com.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.components.BackButton
import app.voqal.com.presentation.onboarding.components.OtpAction
import app.voqal.com.presentation.onboarding.components.OtpInputField
import app.voqal.com.presentation.onboarding.components.OtpState

@Composable
fun OtpScreen(
    state: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoqalTheme.colors.background)
            .padding(horizontal = 24.dp),
    ) {

        Spacer(Modifier.height(30.dp))

        // ✅ BACK BUTTON (LEFT ALIGNED)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            BackButton(onClick = {})
        }

        Spacer(Modifier.height(40.dp))

        // TITLE
        Text(
            text = "Enter Your OTP Code",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = VoqalTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // SUBTITLE
        Text(
            text = "A 6-digit code has been sent to +1 (123) 456-7890",
            fontSize = 14.sp,
            color = VoqalTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(40.dp))

        // 🔢 OTP INPUTS
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
                        onAction(
                            OtpAction.OnEnterNumber(
                                number = newNumber,
                                index = index
                            )
                        )
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

        Spacer(Modifier.height(20.dp))

        // 📌 RESEND + HELP ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Resend Code",
                fontSize = 14.sp,
                color = VoqalTheme.colors.primary
            )

            Text(
                text = "Need Help?",
                fontSize = 14.sp,
                color = VoqalTheme.colors.primary
            )
        }

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VoqalPrimaryButton(
                text = "Let's Go",
                onClick = { /* verify OTP */ },
                enabled = state.isValid == true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpScreenPreview() {

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