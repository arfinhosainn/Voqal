package app.voqal.com.feature.room.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.components.VoqalBottomNavTab
import app.voqal.com.core.components.VoqalBottomNavigationBar
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.domain.InviteParticipantUi
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.ParticipantUi
import app.voqal.com.feature.room.presentation.components.InviteToRoomDialog
import app.voqal.com.feature.room.presentation.components.NewsRoomCard
import app.voqal.com.feature.room.presentation.components.RoomColorVariant

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
    var showInviteDialog by remember { mutableStateOf(false) }

    if (showInviteDialog) {
        InviteToRoomDialog(
            participants = listOf(
                InviteParticipantUi(id = "1", name = "Alex", avatar = null),
                InviteParticipantUi(id = "2", name = "Jordan", avatar = null),
                InviteParticipantUi(id = "3", name = "Taylor", avatar = null),
                InviteParticipantUi(id = "4", name = "Morgan", avatar = null),
                InviteParticipantUi(id = "5", name = "Casey", avatar = null),
                InviteParticipantUi(id = "6", name = "Riley", avatar = null),
                InviteParticipantUi(id = "7", name = "Parker", avatar = null),
                InviteParticipantUi(id = "8", name = "Quinn", avatar = null),
            ),
            onDismissRequest = { showInviteDialog = false }
        )
    }

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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column {
                    NewsRoomCard(
                        room = NewsRoomUi(
                            id = "room_1",
                            category = "NEWS NEWS \uD83C\uDF89",
                            title = "3 Minute News",
                            participants = listOf(
                                ParticipantUi(id = "1", name = "Lena Marsh"),
                                ParticipantUi(id = "2", name = "Minerva Spencer"),
                                ParticipantUi(id = "3", name = "John Carter"),
                            ),
                            listenerCount = 155,
                            commentCount = 3
                        ),
                        colorVariant = RoomColorVariant.BLUE,
                        onMoreClick = { showInviteDialog = true }
                    )

                    Spacer(Modifier.height(12.dp))

                    NewsRoomCard(
                        room = NewsRoomUi(
                            id = "room_2",
                            category = "BUSINESS ENTREPRENEURSHIP",
                            title = "Live Mastermind",
                            participants = listOf(
                                ParticipantUi(id = "4", name = "Jon Daniels"),
                                ParticipantUi(id = "5", name = "Della Guerrero"),
                                ParticipantUi(id = "6", name = "Blake Vega"),
                            ),
                            listenerCount = 49,
                            commentCount = 12
                        ),
                        colorVariant = RoomColorVariant.WARM,
                        onMoreClick = { showInviteDialog = true }
                    )

                    Spacer(Modifier.height(12.dp))

                    NewsRoomCard(
                        room = NewsRoomUi(
                            id = "room_3",
                            category = "TED OFFICIAL",
                            title = "Baajaveri Hedhunu",
                            participants = listOf(
                                ParticipantUi(id = "7", name = "Christina Norton"),
                                ParticipantUi(id = "8", name = "Justin Hart"),
                            ),
                            listenerCount = 15,
                            commentCount = 2
                        ),
                        colorVariant = RoomColorVariant.SLATE,
                        onMoreClick = { showInviteDialog = true }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
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
