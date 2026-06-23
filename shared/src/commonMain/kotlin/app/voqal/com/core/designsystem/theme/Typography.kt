package app.voqal.com.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import org.jetbrains.compose.resources.Font
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.poppins_bold
import voqal.shared.generated.resources.poppins_extrabold
import voqal.shared.generated.resources.poppins_medium
import voqal.shared.generated.resources.poppins_regular
import voqal.shared.generated.resources.poppins_semibold


val BricolageGrotesq
    @Composable get() = FontFamily(
        Font(Res.font.poppins_regular, FontWeight.Normal),
        Font(Res.font.poppins_medium, FontWeight.Medium),
        Font(Res.font.poppins_semibold, FontWeight.SemiBold),
        Font(Res.font.poppins_bold, FontWeight.Bold),
        Font(Res.font.poppins_extrabold, FontWeight.ExtraBold),
    )




val VoqalTypography
    @Composable
    get() = Typography(

        displayLarge = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 34.sp,
            lineHeight = 41.sp
        ),

        // Title 1 — 28 / 34
        titleLarge = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp
        ),

        // Title 2 — 22 / 28
        titleMedium = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),

        // Title 3 — 20 / 25
        titleSmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 25.sp
        ),

        // Headline — 17 / 22
        headlineSmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 22.sp
        ),

        // Body — 17 / 22
        bodyLarge = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 22.sp
        ),

        bodyMedium = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 21.sp
        ),

        bodySmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp
        ),

        labelMedium = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),

        labelSmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
    )
