package app.voqal.com.feature.room.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.components.VoqalBottomNavTab
import app.voqal.com.core.components.VoqalBottomNavigationBar
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun RoomRoot(
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoomScreen(
        onCreateRoomClick = onCreateRoomClick,
        modifier = modifier
    )
}

@Composable
fun RoomScreen(
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoqalTheme.colors.background,
        bottomBar = {
            VoqalBottomNavigationBar(
                selectedTab = VoqalBottomNavTab.Home,
                onTabClick = {
                    // Additional tabs will be wired when their screens exist.
                },
                onCreateRoomClick = onCreateRoomClick,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Rooms",
                color = VoqalTheme.colors.onBackground,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Create or join a room to start talking.",
                color = VoqalTheme.colors.onSurfaceVariant,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomScreenPreview() {
    VoqalTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VoqalTheme.colors.background)
        ) {
            RoomScreen(onCreateRoomClick = {})
        }
    }
}
