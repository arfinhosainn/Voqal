package app.voqal.com.feature.room.presentation.components.bottomsheet

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.presentation.model.RoomType
import org.jetbrains.compose.resources.painterResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.compose_multiplatform
import voqal.shared.generated.resources.locked
import voqal.shared.generated.resources.open
import voqal.shared.generated.resources.social

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomTypeBottomSheet(
    selectedType: RoomType,
    onTypeSelected: (RoomType) -> Unit,
    onStartClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = VoqalTheme.colors.background,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Start a room",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = selectedType.title,
                fontWeight = FontWeight.SemiBold,
                color = VoqalTheme.colors.onBackground,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(24.dp))

            HorizontalDivider(
                color = Color(0xFFEDEDED)
            )

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoomTypeCard(
                    title = "Open",
                    image = painterResource(Res.drawable.open), // Placeholder
                    selected = selectedType == RoomType.OPEN
                ) {
                    onTypeSelected(RoomType.OPEN)
                }

                RoomTypeCard(
                    title = "Social",
                    image = painterResource(Res.drawable.social), // Placeholder
                    selected = selectedType == RoomType.SOCIAL
                ) {
                    onTypeSelected(RoomType.SOCIAL)
                }

                RoomTypeCard(
                    title = "Closed",
                    image = painterResource(Res.drawable.locked), // Placeholder
                    selected = selectedType == RoomType.CLOSED
                ) {
                    onTypeSelected(RoomType.CLOSED)
                }
            }

            Spacer(Modifier.height(48.dp))

            VoqalPrimaryButton(
                text = "Let's Go",
                onClick = onStartClick,
                enabled = !isLoading,
                loading = isLoading,
                shape = VoqalTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = VoqalTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            )

            Spacer(Modifier.height(36.dp))
        }
    }
}
