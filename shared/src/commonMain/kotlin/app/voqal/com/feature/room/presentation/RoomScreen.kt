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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import app.voqal.com.feature.room.presentation.components.HomeTopBar
import app.voqal.com.feature.room.presentation.components.InviteToRoomDialog
import app.voqal.com.feature.room.presentation.components.NewsRoomCard
import app.voqal.com.feature.room.presentation.components.RoomColorVariant
import app.voqal.com.feature.room.presentation.components.bottomsheet.RoomTypeBottomSheet
import app.voqal.com.core.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoomRoot(
    onRoomCreated: (String) -> Unit,
    onRoomClick: (String) -> Unit,
    viewModel: RoomViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RoomEvent.RoomCreated -> onRoomCreated(event.roomId)
            is RoomEvent.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.error.asStringAsync())
                }
            }
        }
    }
    
    RoomScreen(
        state = state,
        onAction = viewModel::onAction,
        onRoomClick = onRoomClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun RoomScreen(
    state: RoomState,
    onAction: (RoomAction) -> Unit,
    onRoomClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var showInviteDialog by remember { mutableStateOf(value = false) }

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
            onDismiss = { onAction(RoomAction.OnDismissSheet) },
            isLoading = state.isCreatingRoom
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoqalTheme.colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            itemsIndexed(state.rooms) { index, room ->
                NewsRoomCard(
                    room = room,
                    colorVariant = RoomColorVariant.forIndex(index),
                    onClick = { onRoomClick(it) },
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
                onAction = {},
                onRoomClick = {},
                snackbarHostState = remember { SnackbarHostState() }
            )
        }
    }
}
