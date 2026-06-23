package app.voqal.com.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VoqalMint,
    onPrimary = VoqalInk,
    primaryContainer = Color(0xFFE4F7EB),
    onPrimaryContainer = VoqalInk,
    secondary = Color(0xFF9BBDA2),
    onSecondary = VoqalInk,
    secondaryContainer = Color(0xFFEAF4EC),
    onSecondaryContainer = VoqalInk,
    tertiary = Color(0xFFFFB49F),
    onTertiary = VoqalInk,
    background = VoqalLightBackground,
    onBackground = VoqalInk,
    surface = VoqalLightBackground,
    onSurface = VoqalInk,
    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = Color(0xFF8F9099),
    outline = Color(0xFFE3E4E8),
    outlineVariant = Color(0xFFEAF1F5),
    scrim = Color(0x99000000),
    inverseSurface = VoqalDarkBackground,
    inverseOnSurface = Color.White,
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = VoqalMintLight,
    onPrimary = VoqalInk,
    primaryContainer = Color(0xFF3E5746),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFA8CBAF),
    onSecondary = VoqalInk,
    secondaryContainer = Color(0xFF3F5044),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFFB49F),
    onTertiary = VoqalInk,
    background = VoqalDarkBackground,
    onBackground = Color.White,
    surface = VoqalDarkBackground,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3B3B3B),
    onSurfaceVariant = Color(0xFFB9BBC2),
    outline = Color(0xFF555555),
    outlineVariant = Color(0xFF3A3A3A),
    scrim = Color(0xCC000000),
    inverseSurface = VoqalLightBackground,
    inverseOnSurface = VoqalInk,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val LocalVoqalExtendedColors = staticCompositionLocalOf {
    LightVoqalExtendedColors
}

object VoqalTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val extendedColors: VoqalExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalVoqalExtendedColors.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes
}

@Composable
fun VoqalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalVoqalExtendedColors provides if (darkTheme) {
            DarkVoqalExtendedColors
        } else {
            LightVoqalExtendedColors
        },
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = VoqalTypography,
            shapes = VoqalShapes,
            content = content,
        )
    }
}
