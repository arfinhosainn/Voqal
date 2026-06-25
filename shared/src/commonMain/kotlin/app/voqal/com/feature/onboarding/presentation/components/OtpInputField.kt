package app.voqal.com.feature.onboarding.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.BricolageGrotesq
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun OtpInputField(
    number: Int?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onNumberChanged: (Int?) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier,
    isFocusedExternal: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    val text = remember(number) {
        TextFieldValue(
            text = number?.toString().orEmpty(),
            selection = TextRange(number?.toString()?.length ?: 0)
        )
    }

    Box(
        modifier = modifier
            .border(
                width = 0.dp,
                color = VoqalTheme.colors.surface
            ).clip(VoqalTheme.shapes.extraSmall)
            .background(if (isSystemInDarkTheme()) VoqalTheme.extendedColors.chip else Color(0xFFF0F0F0)),
        contentAlignment = Alignment.Center
    ) {

        BasicTextField(
            value = text,
            onValueChange = { newValue ->
                val input = newValue.text

                if (input.length <= 1 && input.all { it.isDigit() }) {
                    onNumberChanged(input.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(VoqalTheme.colors.onBackground),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontFamily = BricolageGrotesq,
                fontSize = 30.sp,
                color = VoqalTheme.colors.onBackground,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier
                .padding(5.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event: KeyEvent ->
                    if (event.key == Key.Backspace) {
                        if (number == null) {
                            onKeyboardBack()
                        }
                    }
                    false
                }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun OtpInputFieldFilledPreview() {
    val focusRequester = remember { FocusRequester() }

    OtpInputField(
        number = 0,
        focusRequester = focusRequester,
        onFocusChanged = {},
        onNumberChanged = {},
        onKeyboardBack = {},
        modifier = Modifier.size(80.dp)
    )
}
