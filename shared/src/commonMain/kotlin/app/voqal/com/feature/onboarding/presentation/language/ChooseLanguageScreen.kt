package app.voqal.com.feature.onboarding.presentation.language

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.core.designsystem.theme.BricolageGrotesq
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.OnboardingScaffold
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChooseLanguageRoot(
    onNavigateToNext: (LanguageUi) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LanguageEvent.NavigateToNext -> onNavigateToNext(event.chosenLanguage)
        }
    }

    ChooseLanguageScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun ChooseLanguageScreen(
    state: LanguageState,
    onBack: () -> Unit,
    onAction: (LanguageAction) -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        currentStep = 6
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Choose language",
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
                text = "Select the language you speak most often",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = VoqalTheme.colors.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            LanguageSearchField(
                value = state.searchQuery,
                onValueChange = { onAction(LanguageAction.OnSearchQueryChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (state.filteredLanguages.isEmpty()) {
                    Text(
                        text = "No languages found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        color = VoqalTheme.colors.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 140.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.filteredLanguages,
                            key = { it.id }
                        ) { language ->
                            LanguageItem(
                                language = language,
                                selected = state.selectedLanguage?.id == language.id,
                                onClick = { onAction(LanguageAction.OnLanguageSelect(language)) }
                            )
                        }
                    }
                }

                // Smooth background gradient bottom overlay structure
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    VoqalTheme.colors.background.copy(alpha = 0.8f),
                                    VoqalTheme.colors.background
                                )
                            )
                        )
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    VoqalPrimaryButton(
                        text = "Choose language",
                        enabled = state.selectedLanguage != null && !state.isSubmitting,
                        loading = state.isSubmitting,
                        onClick = { onAction(LanguageAction.OnContinueClick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Search languages",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = BricolageGrotesq,
                color = VoqalTheme.colors.onSurfaceVariant
            )
        },
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = BricolageGrotesq,
            color = VoqalTheme.colors.onBackground
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Search
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = VoqalTheme.colors.surfaceVariant,
            unfocusedContainerColor = VoqalTheme.colors.surfaceVariant,
            disabledContainerColor = VoqalTheme.colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = VoqalTheme.colors.primary
        )
    )
}

@Composable
private fun LanguageItem(
    language: LanguageUi,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderWidth by animateDpAsState(
        targetValue = if (selected) 3.dp else 1.dp,
        label = "LanguageBorderWidth"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            VoqalTheme.colors.primary
        } else {
            VoqalTheme.colors.onSurfaceVariant.copy(alpha = 0.2f) // Fallback default themed color
        },
        label = "LanguageBorderColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            VoqalTheme.colors.primary.copy(alpha = 0.08f)
        } else {
            Color.Transparent
        },
        label = "LanguageBackgroundColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        label = "LanguageCardScale"
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
                contentDescription = "${language.name} Flag Indicator",
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

@PreviewLightDark
@Composable
private fun PreviewChooseLanguageScreen() {
    VoqalTheme {
        ChooseLanguageScreen(
            state = LanguageState(),
            onBack = {},
            onAction = {}
        )
    }
}
