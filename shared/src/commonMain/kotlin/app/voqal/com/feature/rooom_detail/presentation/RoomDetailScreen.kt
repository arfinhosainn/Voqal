package app.voqal.com.feature.rooom_detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.feature.rooom_detail.presentation.components.RoomDetailTopBar
import app.voqal.com.feature.rooom_detail.presentation.components.participant.ParticipantAvatar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_hand
import voqal.shared.generated.resources.ic_more
import voqal.shared.generated.resources.ic_send

@Composable
fun RoomDetailRoot(
    onLeave: () -> Unit,
    viewModel: RoomDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RoomDetailEvent.LeaveRoom -> onLeave()
            is RoomDetailEvent.ShowError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.error.asStringAsync())
                }
            }
        }
    }

    RoomDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RoomDetailScreen(
    state: RoomDetailState,
    onAction: (RoomDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        BottomBarAction(
            icon = vectorResource(Res.drawable.ic_send),
            contentDescription = "Mic",
            onClick = { onAction(RoomDetailAction.OnMicClick) }
        ),
        BottomBarAction(
            icon = vectorResource(Res.drawable.ic_hand),
            contentDescription = "Hand",
            onClick = { onAction(RoomDetailAction.OnHandClick) }
        ),
        BottomBarAction(
            icon = vectorResource(Res.drawable.ic_more),
            contentDescription = "More",
            onClick = { onAction(RoomDetailAction.OnMoreClick) }
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoqalTheme.colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RoomDetailTopBar(
                greeting = "Good morning",
                name = "Marian Marsh",
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            RoomDetailBottomBar(
                actions = actions,
                isHost = state.isHost,
                onLeave = { onAction(RoomDetailAction.OnLeaveClick) },
                onEnd = { onAction(RoomDetailAction.OnEndClick) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VoqalTheme.colors.primary
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(5.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(state.participants, key = { it.id }) { participant ->
                    ParticipantAvatar(
                        state = participant,
                        onClick = { /* Handle participant click */ }
                    )
                }
            }
        }
    }
}
