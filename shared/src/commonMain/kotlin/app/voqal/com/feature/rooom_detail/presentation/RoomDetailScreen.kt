package app.voqal.com.feature.rooom_detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.feature.rooom_detail.presentation.components.RoomDetailTopBar
import app.voqal.com.feature.rooom_detail.presentation.components.participant.ParticipantAvatar
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.vectorResource
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

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RoomDetailEvent.LeaveRoom -> onLeave()
            is RoomDetailEvent.ShowError -> {
                // TODO: Show snackbar or error dialog
            }
        }
    }

    RoomDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun RoomDetailScreen(
    state: RoomDetailState,
    onAction: (RoomDetailAction) -> Unit,
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
                onLeave = { onAction(RoomDetailAction.OnLeaveClick) }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
