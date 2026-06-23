package app.voqal.com.presentation.onboarding


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.presentation.onboarding.components.BackButton


@Composable
fun PickUsernameScreen(
    onBack: () -> Unit,
    onContinue: (firstName: String, lastName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val isFormValid = firstName.isNotBlank() && lastName.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoqalTheme.colors.background)
            .padding(horizontal = 24.dp),
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        BackButton(onClick = onBack)

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pick a username?",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = VoqalTheme.colors.onBackground,
                lineHeight = 36.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose a distinct username",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = VoqalTheme.colors.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        UserNameField(
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = "@Superboy",
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VoqalPrimaryButton(
                text = "Let's Go",
                onClick = { onContinue(firstName.trim(), lastName.trim()) },
                enabled = isFormValid,
            )
        }
    }
}



@Composable
private fun UserNameField(
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

@Preview
@Composable
private fun FullNameScreenPreview() {
    VoqalTheme {
        PickUsernameScreen(
            onBack = {},
            onContinue = { _, _ -> },
        )
    }
}