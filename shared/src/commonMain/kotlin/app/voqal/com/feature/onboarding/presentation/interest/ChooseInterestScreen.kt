package app.voqal.com.feature.onboarding.presentation.interest

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.OnboardingScaffold

data class InterestItem(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseInterestsScreen(
    onBack: () -> Unit,
    onContinue: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val repositoryInterests = remember {
        listOf(
            // 🚀 Tech & Innovation
            InterestItem("1", "AI & Futurism", "🤖", "Tech & Innovation"),
            InterestItem("2", "Startups & VC", "🚀", "Tech & Innovation"),
            InterestItem("3", "Coding & Dev", "💻", "Tech & Innovation"),
            InterestItem("4", "Crypto & Web3", "🪙", "Tech & Innovation"),
            InterestItem("5", "Gadgets & Gear", "🔌", "Tech & Innovation"),

            // 📈 Business & Money
            InterestItem("6", "Investing & Stocks", "📈", "Business & Money"),
            InterestItem("7", "Marketing & Growth", "📣", "Business & Money"),
            InterestItem("8", "Side Hustles", "💼", "Business & Money"),
            InterestItem("9", "Real Estate", "🏢", "Business & Money"),
            InterestItem("10", "Creator Economy", "📸", "Business & Money"),

            // 🎵 Entertainment & Arts
            InterestItem("11", "Music & Beats", "🎵", "Entertainment & Arts"),
            InterestItem("12", "Movies & Shows", "🎬", "Entertainment & Arts"),
            InterestItem("13", "Gaming & Esports", "🎮", "Entertainment & Arts"),
            InterestItem("14", "Anime & Manga", "🎏", "Entertainment & Arts"),
            InterestItem("15", "Standup Comedy", "🎤", "Entertainment & Arts"),

            // 🧠 Health & Mindset
            InterestItem("16", "Mental Health", "🧠", "Health & Mindset"),
            InterestItem("17", "Fitness & Gym", "💪", "Health & Mindset"),
            InterestItem("18", "Meditation & Zen", "🧘", "Health & Mindset"),
            InterestItem("19", "Biohacking & Diet", "🧪", "Health & Mindset"),

            // 🌍 Lifestyle & Culture
            InterestItem("20", "Travel & Adventure", "✈️", "Lifestyle & Culture"),
            InterestItem("21", "Books & Literature", "📚", "Lifestyle & Culture"),
            InterestItem("22", "Food & Cooking", "🍳", "Lifestyle & Culture"),
            InterestItem("23", "Dating & Relationships", "❤️", "Lifestyle & Culture"),
            InterestItem("24", "Fashion & Style", "✨", "Lifestyle & Culture"),

            // 🏛️ Deep Conversations
            InterestItem("25", "Philosophy", "🏛️", "Deep Conversations"),
            InterestItem("26", "True Crime & Mystery", "🕵️", "Deep Conversations"),
            InterestItem("27", "History & Myths", "📜", "Deep Conversations"),
            InterestItem("28", "Current Events", "🌍", "Deep Conversations"),
            InterestItem("29", "Pop Culture", "🍿", "Deep Conversations")
        )
    }

    var selectedInterestIds by remember { mutableStateOf(setOf<String>()) }
    val categories = remember(repositoryInterests) { repositoryInterests.groupBy { it.category } }
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))

            // --- HEADER TITLE & TAGLINE (Padded horizontally) ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Choose your interest",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = VoqalTheme.colors.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Choose your preferred interest",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = VoqalTheme.colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            // --- CONTENT AREA WITH FLOATING GRADIENT OVERLAY ---
            Box(modifier = Modifier.weight(1f)) {

                // 1. Scrollable List of Categories & Chips
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    categories.forEach { (categoryName, items) ->
                        Text(
                            text = categoryName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VoqalTheme.colors.onBackground,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items.forEach { item ->
                                val isSelected = selectedInterestIds.contains(item.id)
                                InterestChip(
                                    item = item,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedInterestIds = if (isSelected) {
                                            selectedInterestIds - item.id
                                        } else {
                                            selectedInterestIds + item.id
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // Crucial: Padding spacer allowing elements to scroll clear of the button layout
                    Spacer(Modifier.height(140.dp))
                }

                // 2. The Floating Transparent Gradient Button Container
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
                        .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    VoqalPrimaryButton(
                        text = "Looks good",
                        enabled = selectedInterestIds.isNotEmpty(),
                        onClick = { onContinue(selectedInterestIds.toList()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun InterestChip(
    item: InterestItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) VoqalTheme.colors.primary else VoqalTheme.colors.surfaceVariant,
        label = "chipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) VoqalTheme.colors.onPrimary else VoqalTheme.colors.onBackground,
        label = "chipContent"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = item.emoji, fontSize = 15.sp)

        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )

        Icon(
            imageVector = if (isSelected) Icons.Default.Close else Icons.Default.Add,
            contentDescription = if (isSelected) "Deselect" else "Select",
            tint = contentColor.copy(alpha = 0.75f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewChooseInterestsScreen() {
    VoqalTheme {
        ChooseInterestsScreen(onBack = {}, onContinue = {})
    }
}