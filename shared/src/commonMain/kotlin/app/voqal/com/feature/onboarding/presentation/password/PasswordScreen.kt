package app.voqal.com.feature.onboarding.presentation.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.Poppins
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.feature.onboarding.OnboardingScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PasswordRoot(
    isNewUser: Boolean,
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PasswordEvent.NavigateToNext -> onNavigateToNext()
            is PasswordEvent.ShowSnackbar -> { /* Hook up snackbar host when available */ }
        }
    }

    PasswordScreen(
        state = state,
        isNewUser = isNewUser,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun PasswordScreen(
    state: PasswordState,
    isNewUser: Boolean,
    onBack: () -> Unit,
    onAction: (PasswordAction) -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        showTopBar = false,
        currentStep = 2 // Assuming this is step 2 after email
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
                    text = if (isNewUser) "Set new password" else "Enter password",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = VoqalTheme.colors.onBackground,
                    lineHeight = 36.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isNewUser) "Secure your account" else "Welcome back!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = VoqalTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            PasswordField(
                value = state.password,
                onValueChange = { onAction(PasswordAction.OnPasswordChange(it)) },
                onDone = { onAction(PasswordAction.OnContinueClick) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot password
            Text(
                text = "Forgot password?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VoqalTheme.colors.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // New text with bold underline
            Text(
                text = AnnotatedString("Login with magic link"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = VoqalTheme.colors.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.weight(1f))

            VoqalPrimaryButton(
                text = if (isNewUser)"Set Password" else "Change Password",
                onClick = { onAction(PasswordAction.OnContinueClick) },
                enabled = state.isFormValid && !state.isSubmitting,
                loading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {},
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "********",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = VoqalTheme.colors.onSurfaceVariant,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    tint = VoqalTheme.colors.onSurfaceVariant
                )
            }
        },
        shape = RoundedCornerShape(25.dp),
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            color = VoqalTheme.colors.onBackground
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
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

@PreviewLightDark
@Composable
private fun PasswordScreenNewUserPreview() {
    VoqalTheme {
        PasswordScreen(
            state = PasswordState(),
            isNewUser = true,
            onBack = {},
            onAction = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PasswordScreenExistingUserPreview() {
    VoqalTheme {
        PasswordScreen(
            state = PasswordState(),
            isNewUser = false,
            onBack = {},
            onAction = {}
        )
    }
}
