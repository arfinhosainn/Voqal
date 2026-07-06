package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun EndRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = VoqalTheme.colors.surface,
        title = {
            Text(
                text = "Everyone's listening…",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = VoqalTheme.colors.onSurface
            )
        },
        text = {
            Text(
                text = "You're the host. Leaving now will end the room and disconnect everyone who's joined your conversation.",
                style = MaterialTheme.typography.bodyMedium,
                color = VoqalTheme.colors.onSurfaceVariant
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935), // Red
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("End Room", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = VoqalTheme.colors.onSurface
                    ),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Stay", fontWeight = FontWeight.SemiBold)
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        color = VoqalTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        dismissButton = null,
        modifier = Modifier.padding(16.dp)
    )
}

@PreviewLightDark
@Composable
fun PreviewEndRoomDialog() {
    VoqalTheme {
        EndRoomDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}
