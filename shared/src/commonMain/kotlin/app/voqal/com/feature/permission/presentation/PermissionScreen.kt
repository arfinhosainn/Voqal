package app.voqal.com.feature.permission.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun PermissionScreen(
    state: PermissionScreenState,
    onAction: (PermissionScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoqalTheme.colors.background,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                VoqalPrimaryButton(
                    text = "Continue",
                    onClick = { onAction(PermissionScreenAction.OnRequestClick) },
                    modifier = Modifier.fillMaxWidth(),
                    loading = state.isRequesting
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.emoji,
                fontSize = 40.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = state.title,
                style = VoqalTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                ),
                color = VoqalTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
            
            if (state.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.description,
                    style = VoqalTheme.typography.bodyMedium,
                    color = VoqalTheme.extendedColors.mutedText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
