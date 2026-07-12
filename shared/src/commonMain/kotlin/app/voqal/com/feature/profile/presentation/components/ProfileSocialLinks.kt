package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.profile.presentation.model.SocialLinkUi

@Composable
fun ProfileSocialLinks(
    links: List<SocialLinkUi>,
    onLinkClick: (SocialLinkUi) -> Unit,
    onAddClick: () -> Unit,
) {
    if (links.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            links.forEach { link ->
                SocialLinkRow(link = link, onClick = { onLinkClick(link) })
            }
        }
    } else {
        AddSocialLinkButton(onClick = onAddClick)
    }
}

@Composable
private fun SocialLinkRow(
    link: SocialLinkUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(VoqalTheme.shapes.extraSmall)
                .background(VoqalTheme.extendedColors.chip),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = link.label.take(2).uppercase(),
                color = VoqalTheme.extendedColors.mutedText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = link.label,
            color = VoqalTheme.colors.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = link.value?.ifBlank { "Add Social" } ?: "Add Social",
            color = if (link.value.isNullOrBlank()) VoqalTheme.colors.primary else VoqalTheme.colors.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AddSocialLinkButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .width(300.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(
                color = VoqalTheme.colors.surfaceContainerHighest,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = VoqalTheme.colors.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Social",
                style = VoqalTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = VoqalTheme.colors.primary,
            )
        }
    }
}
