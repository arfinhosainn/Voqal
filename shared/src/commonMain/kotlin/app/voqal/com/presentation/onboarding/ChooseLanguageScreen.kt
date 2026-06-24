package app.voqal.com.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.chinese
import voqal.shared.generated.resources.czech
import voqal.shared.generated.resources.danish
import voqal.shared.generated.resources.english
import voqal.shared.generated.resources.finnish
import voqal.shared.generated.resources.french
import voqal.shared.generated.resources.german
import voqal.shared.generated.resources.hungarian
import voqal.shared.generated.resources.indonesian
import voqal.shared.generated.resources.irish
import voqal.shared.generated.resources.italian
import voqal.shared.generated.resources.korean
import voqal.shared.generated.resources.persian
import voqal.shared.generated.resources.polish
import voqal.shared.generated.resources.russian
import voqal.shared.generated.resources.spanish
import voqal.shared.generated.resources.swedish
import voqal.shared.generated.resources.turkish
import voqal.shared.generated.resources.ukrainian

@Composable
fun ChooseLanguageScreen(
    onBack: () -> Unit,
    onContinue: (LanguageUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = remember {
        listOf(
            LanguageUi("de", "German", Res.drawable.german),
            LanguageUi("zh", "Chinese", Res.drawable.chinese),
            LanguageUi("ko", "Korean", Res.drawable.korean),
            LanguageUi("en", "English", Res.drawable.english),
            LanguageUi("fr", "French", Res.drawable.french),
            LanguageUi("id", "Indonesian", Res.drawable.indonesian),
            LanguageUi("fa", "Persian", Res.drawable.persian),
            LanguageUi("hu", "Hungarian", Res.drawable.hungarian),
            LanguageUi("it", "Italian", Res.drawable.italian),
            LanguageUi("sv", "Swedish", Res.drawable.swedish),
            LanguageUi("cs", "Czech", Res.drawable.czech),
            LanguageUi("da", "Danish", Res.drawable.danish),
            LanguageUi("fi", "Finnish", Res.drawable.finnish),
            LanguageUi("ga", "Irish", Res.drawable.irish),
            LanguageUi("pl", "Polish", Res.drawable.polish),
            LanguageUi("ru", "Russian", Res.drawable.russian),
            LanguageUi("es", "Spanish", Res.drawable.spanish),
            LanguageUi("tr", "Turkish", Res.drawable.turkish),
            LanguageUi("uk", "Ukrainian", Res.drawable.ukrainian),
        )
    }

    var selectedLanguage by remember { mutableStateOf<LanguageUi?>(null) }

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- HEADER SECTION ---
            Spacer(Modifier.height(16.dp)) // Optional: slightly offset from the app bar

            Text(
                text = "Choose Language",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = VoqalTheme.colors.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Select the language you speak",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = VoqalTheme.colors.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            Box(modifier = Modifier.weight(1f)) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                    // Important: Padding allows scrolling past the floating bottom button
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 140.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = languages,
                        key = { it.id }
                    ) { language ->
                        LanguageItem(
                            language = language,
                            selected = selectedLanguage == language,
                            onClick = { selectedLanguage = language }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(140.dp) // Make it taller than the button for a smooth fade
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent, // Starts transparent at the top
                                    VoqalTheme.colors.background.copy(alpha = 0.8f), // Gets mostly solid
                                    VoqalTheme.colors.background // Fully solid at the bottom edge
                                )
                            )
                        )
                        .padding(bottom = 24.dp), // Lift the button off the bottom edge
                    contentAlignment = Alignment.BottomCenter
                ) {
                    VoqalPrimaryButton(
                        text = "Let's Go",
                        enabled = selectedLanguage != null,
                        onClick = {
                            selectedLanguage?.let(onContinue)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: LanguageUi,
    selected: Boolean,
    onClick: () -> Unit,
) {

    val borderWidth by animateDpAsState(
        targetValue = if (selected) 3.dp else 1.dp,
        label = "borderWidth"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected)
            VoqalTheme.colors.primary
        else
            Color(0xFFE0E0E0),
        label = "borderColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected)
            VoqalTheme.colors.primary.copy(alpha = 0.08f)
        else
            Color.Transparent,
        label = "backgroundColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(88.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(language.flag),
                contentDescription = language.name,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = language.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = VoqalTheme.colors.onBackground
        )
    }
}

data class LanguageUi(
    val id: String,
    val name: String,
    val flag: DrawableResource
)

@PreviewLightDark
@Composable
fun PreviewChooseLanguageScreen(){
    VoqalTheme {
        ChooseLanguageScreen(onBack = {}, onContinue = {})
    }
}