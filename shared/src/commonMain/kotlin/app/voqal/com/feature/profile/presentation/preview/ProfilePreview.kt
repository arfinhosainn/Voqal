package app.voqal.com.feature.profile.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.profile.presentation.components.ProfileContent

@PreviewLightDark
@Composable
fun ProfilePreview() {
    VoqalTheme {
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        ) {
            ProfileContent(
                profile = ProfilePreviewData.profile,
                actions = ProfilePreviewData.actions,
            )
        }
    }
}
