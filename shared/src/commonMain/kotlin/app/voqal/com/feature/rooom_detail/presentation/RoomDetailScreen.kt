package app.voqal.com.feature.rooom_detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.ObserveAsEvents
import app.voqal.com.feature.chat.presentation.ChatEvent
import app.voqal.com.feature.chat.presentation.ChatViewModel
import app.voqal.com.feature.chat.presentation.TransformingChatLayout
import app.voqal.com.feature.chat.presentation.TransformingChatSheet
import app.voqal.com.feature.rooom_detail.presentation.components.EndRoomDialog
import app.voqal.com.feature.rooom_detail.presentation.components.RaiseHandSheet
import app.voqal.com.feature.rooom_detail.presentation.components.RoomDetailTopBar
import app.voqal.com.feature.rooom_detail.presentation.components.participant.ParticipantAvatar
import app.voqal.com.feature.rooom_detail.presentation.model.RoomPresentationState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_hand
import voqal.shared.generated.resources.ic_mic
import voqal.shared.generated.resources.ic_micoff
import voqal.shared.generated.resources.ic_send

@Composable
fun RoomDetailRoot(
    onLeave: () -> Unit,
    viewModel: RoomDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val chatViewModel = koinViewModel<ChatViewModel> { parametersOf(state.roomId) }
    val chatState by chatViewModel.state.collectAsStateWithLifecycle()
    val messagesListState = rememberLazyListState() // Hoisted
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
            is RoomDetailEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message.asStringAsync())
                }
            }
        }
    }

    if (state.presentationState == RoomPresentationState.Minimized) {
        onLeave()
    }

    ExpandedRoomContent(
        state = state,
        chatState = chatState,
        onAction = viewModel::onAction,
        onChatEvent = chatViewModel::onEvent,
        messagesListState = messagesListState,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedRoomContent(
    state: RoomDetailState,
    chatState: app.voqal.com.feature.chat.presentation.ChatUiState,
    onAction: (RoomDetailAction) -> Unit,
    onChatEvent: (ChatEvent) -> Unit,
    messagesListState: androidx.compose.foundation.lazy.LazyListState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // Sync isVisible with sheetState
    LaunchedEffect(state.isChatVisible) {
        if (state.isChatVisible) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    // Handle dismissal via gesture
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded && state.isChatVisible) {
            onAction(RoomDetailAction.OnDismissChatSheet)
        }
    }

    val actions = listOf(
        BottomBarAction(
            icon = vectorResource(Res.drawable.ic_send),
            contentDescription = "Send",
            onClick = { onAction(RoomDetailAction.OnChatClick) }
        ),
        BottomBarAction(
            icon = vectorResource(Res.drawable.ic_hand),
            contentDescription = "Hand",
            onClick = { onAction(RoomDetailAction.OnHandClick) }
        ),
        BottomBarAction(
            icon = if (state.isMicrophoneEnabled)
                vectorResource(Res.drawable.ic_mic)
            else
                vectorResource(Res.drawable.ic_micoff),
            contentDescription = "Mic",
        ) {
            onAction(RoomDetailAction.OnMicClick)
        }
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val fullHeight = maxHeight
        val fullHeightPx = with(density) { fullHeight.toPx() }
        val peekHeight = 120.dp
        val peekHeightPx = with(density) { peekHeight.toPx() }
        val expandedHeight = fullHeight * 0.3f
        val expandedHeightPx = with(density) { expandedHeight.toPx() }

        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = peekHeight,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            sheetTonalElevation = 0.dp,
            sheetShadowElevation = 0.dp,
            sheetContainerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                RoomDetailTopBar(
                    greeting = "Good morning",
                    name = "Marian Marsh",
                    modifier = Modifier,
                    onMinimizeClick = { onAction(RoomDetailAction.OnMinimizeClick) }
                )
            },
            sheetContent = {
                val progress by remember(fullHeightPx) {
                    derivedStateOf {
                        val offset = try {
                            scaffoldState.bottomSheetState.requireOffset()
                        } catch (e: Exception) {
                            fullHeightPx - peekHeightPx
                        }

                        val partiallyExpandedOffset = fullHeightPx - peekHeightPx
                        val expandedOffset = fullHeightPx - expandedHeightPx

                        if (partiallyExpandedOffset != expandedOffset) {
                            ((partiallyExpandedOffset - offset) / (partiallyExpandedOffset - expandedOffset)).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                    }
                }

                TransformingChatSheet(
                    progress = progress,
                    onDismiss = { onAction(RoomDetailAction.OnDismissChatSheet) }
                ) { transition ->
                    TransformingChatLayout(
                        transition = transition,
                        chatState = chatState,
                        onChatEvent = { event ->
                            if (event == ChatEvent.Dismiss) {
                                onAction(RoomDetailAction.OnDismissChatSheet)
                            } else {
                                onChatEvent(event)
                            }
                        },
                        roomActions = actions,
                        isHost = state.isHost,
                        onLeaveRoom = { onAction(RoomDetailAction.OnLeaveClick) },
                        messagesListState = messagesListState
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (state.isEndRoomDialogVisible) {
                    EndRoomDialog(
                        onDismiss = { onAction(RoomDetailAction.OnDismissEndRoomDialog) },
                        onConfirm = { onAction(RoomDetailAction.OnEndClick) }
                    )
                }

                RaiseHandSheet(
                    isVisible = state.isRaiseHandSheetVisible,
                    onDismiss = { onAction(RoomDetailAction.OnDismissRaiseHandSheet) },
                    onRaiseHand = { onAction(RoomDetailAction.OnConfirmRaiseHand) }
                )

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
}
