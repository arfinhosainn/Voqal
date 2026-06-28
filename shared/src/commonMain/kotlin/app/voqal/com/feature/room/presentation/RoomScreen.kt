package app.voqal.com.feature.room.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.components.VoqalBottomNavTab
import app.voqal.com.core.components.VoqalBottomNavigationBar
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.domain.InviteParticipantUi
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.ParticipantUi
import app.voqal.com.feature.room.presentation.components.HomeTopBar
import app.voqal.com.feature.room.presentation.components.InviteToRoomDialog
import app.voqal.com.feature.room.presentation.components.NewsRoomCard
import app.voqal.com.feature.room.presentation.components.RoomColorVariant
import app.voqal.com.feature.room.presentation.components.bottomsheet.RoomTypeBottomSheet
import app.voqal.com.feature.room.presentation.model.RoomType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoomRoot(
    viewModel: RoomViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    RoomScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun RoomScreen(
    state: RoomState,
    onAction: (RoomAction) -> Unit,
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

    if (state.showRoomTypeSheet) {
        RoomTypeBottomSheet(
            selectedType = state.selectedRoomType,
            onTypeSelected = { onAction(RoomAction.OnRoomTypeSelected(it)) },
            onStartClick = { onAction(RoomAction.OnStartClick) },
            onDismiss = { onAction(RoomAction.OnDismissSheet) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoqalTheme.colors.background,
        topBar = {
            HomeTopBar(
                greeting = "Good morning",
                name = "Marian Marsh",
                onAvatarClick = { /* Handle avatar click */ },
                onSearchClick = { /* Handle search click */ },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            VoqalBottomNavigationBar(
                selectedTab = VoqalBottomNavTab.Home,
                onTabClick = {
                    // Additional tabs will be wired when their screens exist.
                },
                onCreateRoomClick = { onAction(RoomAction.OnCreateRoomClick) },
            )
        }
    ) { innerPadding ->
        val rooms = listOf(
            NewsRoomUi(
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
            NewsRoomUi(
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
            NewsRoomUi(
                id = "room_3",
                category = "TED OFFICIAL",
                title = "Baajaveri Hedhunu",
                participants = listOf(
                    ParticipantUi(id = "7", name = "Christina Norton"),
                    ParticipantUi(id = "8", name = "Justin Hart"),
                ),
                listenerCount = 15,
                commentCount = 2
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(rooms) { index, room ->
                NewsRoomCard(
                    room = room,
                    colorVariant = RoomColorVariant.forIndex(index),
                    onMoreClick = { showInviteDialog = true }
                )
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
            RoomScreen(
                state = RoomState(),
                onAction = {}
            )
        }
    }
}
