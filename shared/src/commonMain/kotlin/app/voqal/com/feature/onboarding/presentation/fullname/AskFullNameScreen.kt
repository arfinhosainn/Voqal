package app.voqal.com.feature.onboarding.presentation.fullname

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.OnboardingScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FullNameRoot(
    onNavigate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FullNameViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FullNameEvent.Navigate -> onNavigate()
            is FullNameEvent.ShowSnackbar -> { /* Trigger snackbar workflow if required */ }
        }
    }

    FullNameScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun FullNameScreen(
    state: FullNameState,
    onBack: () -> Unit,
    onAction: (FullNameAction) -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Header Section ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What's your full name?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = VoqalTheme.colors.onBackground,
                    lineHeight = 36.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "People use real names on Voqal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = VoqalTheme.colors.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- Text Input Fields (Purely driven by hoisted ViewModel State) ---
            NameField(
                value = state.firstName,
                onValueChange = { onAction(FullNameAction.OnFirstNameChange(it)) },
                placeholder = "First",
                imeAction = ImeAction.Next,
            )

            Spacer(modifier = Modifier.height(16.dp))

            NameField(
                value = state.lastName,
                onValueChange = { onAction(FullNameAction.OnLastNameChange(it)) },
                placeholder = "Last",
                imeAction = ImeAction.Done,
                onDone = {
                    if (state.isFormValid) onAction(FullNameAction.OnContinueClick)
                }
            )

            // Pushes the button to the bottom dynamically
            Spacer(modifier = Modifier.weight(1f))

            VoqalPrimaryButton(
                text = "Let's Go",
                onClick = { onAction(FullNameAction.OnContinueClick) },
                enabled = state.isFormValid,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction,
    onDone: () -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
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
            color = VoqalTheme.colors.onBackground
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = imeAction,
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

//@Preview
//@Composable
//private fun FullNameScreenPreview() {
//    VoqalTheme {
//        FullNameScreen(
//            onBack = {},
//            onContinue = { _, _ -> },
//        )
//    }
//}