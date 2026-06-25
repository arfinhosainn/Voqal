package app.voqal.com.feature.onboarding.presentation.interest

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.onboarding.OnboardingScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChooseInterestsRoot(
    onNavigateToNext: (List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseInterestsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ChooseInterestsEvent.NavigateToNext -> onNavigateToNext(event.selectedIds)
        }
    }

    ChooseInterestsScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseInterestsScreen(
    state: ChooseInterestsState,
    onBack: () -> Unit,
    onAction: (ChooseInterestsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        currentStep = 7
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))

            // --- Header Title Section ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Choose your interests",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = VoqalTheme.colors.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Select a few topics you want to follow",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = VoqalTheme.colors.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${state.selectedInterestIds.size} selected - choose at least ${state.minimumSelectionCount}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (state.canContinue) {
                        VoqalTheme.colors.primary
                    } else {
                        VoqalTheme.colors.onSurfaceVariant
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            // --- Categories Display & Floating Overlay Area ---
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 140.dp)
                ) {
                    state.categorizedInterests.forEach { (categoryName, items) ->
                        item(key = "category-$categoryName") {
                            Text(
                                text = categoryName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = VoqalTheme.colors.onBackground,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }

                        item(key = "interests-$categoryName") {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items.forEach { item ->
                                    key(item.id) {
                                        val isSelected = state.selectedInterestIds.contains(item.id)
                                        InterestChip(
                                            item = item,
                                            isSelected = isSelected,
                                            onClick = {
                                                onAction(
                                                    ChooseInterestsAction.OnInterestToggle(item.id)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        item(key = "category-spacer-$categoryName") {
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }

                // --- Absolute Overlay Gradient Layer ---
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
                        text = "Finish",
                        enabled = state.canContinue && !state.isSubmitting,
                        loading = state.isSubmitting,
                        onClick = { onAction(ChooseInterestsAction.OnContinueClick) },
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
        label = "InterestChipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) VoqalTheme.colors.onPrimary else VoqalTheme.colors.onBackground,
        label = "InterestChipContentColor"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            )
            .defaultMinSize(minHeight = 48.dp)
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
            contentDescription = if (isSelected) "Deselect Interest Item" else "Select Interest Item",
            tint = contentColor.copy(alpha = 0.75f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewChooseInterestsScreen() {
    VoqalTheme {
        ChooseInterestsScreen(
            state = ChooseInterestsState(),
            onBack = {},
            onAction = {}
        )
    }
}
