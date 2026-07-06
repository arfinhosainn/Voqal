package app.voqal.com.navigation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.voqal.com.core.components.VoqalBottomNavigationBar
import app.voqal.com.core.presentation.util.ImagePicker
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingGraph
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingRoute
import app.voqal.com.feature.onboarding.presentation.navigation.onboardingNavGraph
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.toParticipantAvatarUiState
import app.voqal.com.feature.room.presentation.navigation.RoomGraph
import app.voqal.com.feature.room.presentation.navigation.roomGraph
import app.voqal.com.feature.rooom_detail.presentation.RoomDetailRoot
import app.voqal.com.feature.rooom_detail.presentation.RoomPresentationStore
import app.voqal.com.feature.rooom_detail.presentation.components.MiniRoomBar
import app.voqal.com.feature.rooom_detail.presentation.model.RoomPresentationState
import app.voqal.com.feature.splash.presentation.SplashScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object SplashRoute

@Composable
fun AppNavHost(
    imagePicker: ImagePicker,
    initialRoomId: String? = null,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val onboardingDraftStore = koinInject<OnboardingDraftStore>()
    val presentationStore = koinInject<RoomPresentationStore>()
    val roomCallDataSource = koinInject<RoomCallRemoteDataSource>()
    val bottomNavStore = koinInject<BottomNavStore>()

    val presentationState by presentationStore.presentationState.collectAsStateWithLifecycle()
    val roomInfo by roomCallDataSource.roomInfo.collectAsStateWithLifecycle()
    val participants by roomCallDataSource.participants.collectAsStateWithLifecycle()
    val isMicEnabled by roomCallDataSource.isMicrophoneEnabled.collectAsStateWithLifecycle()
    val activeRoomId by presentationStore.activeRoomId.collectAsStateWithLifecycle()

    val selectedTab by bottomNavStore.selectedTab.collectAsStateWithLifecycle()
    val isBottomBarVisible by bottomNavStore.isVisible.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        // Simple logic to show bottom bar on Room list
        // In a real app, this might be based on destination ID or custom metadata
        val showBottomBar = currentRoute?.contains("Rooms") == true
        bottomNavStore.setVisible(showBottomBar)
    }

    val startDestination = if (initialRoomId != null) {
        OnboardingRoute.RoomDetailRoute(roomId = initialRoomId, asHost = false)
    } else {
        SplashRoute
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                VoqalBottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabClick = { bottomNavStore.onTabClick(it) },
                    onCreateRoomClick = {
                        bottomNavStore.onCreateRoomClick()
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable<OnboardingRoute.RoomDetailRoute> {
                    RoomDetailRoot(
                        onLeave = {
                            navController.navigate(RoomGraph) {
                                popUpTo(OnboardingRoute.RoomDetailRoute(roomId = "", asHost = false)) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable<SplashRoute> {
                    SplashScreen(
                        onAuthenticated = {
                            navController.navigate(RoomGraph) {
                                popUpTo(SplashRoute) { inclusive = true }
                            }
                        },
                        onNotAuthenticated = {
                            navController.navigate(OnboardingGraph) {
                                popUpTo(SplashRoute) { inclusive = true }
                            }
                        }
                    )
                }

                onboardingNavGraph(
                    navController = navController,
                    onOnboardingComplete = {
                        onboardingDraftStore.clear()
                        navController.navigate(RoomGraph) {
                            popUpTo(OnboardingGraph) { inclusive = true }
                        }
                    },
                    imagePicker = imagePicker
                )

                roomGraph(
                    navController = navController,
                )
            }

            AnimatedVisibility(
                visible = presentationState == RoomPresentationState.Minimized,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
            MiniRoomBar(
                roomName = roomInfo.title.orEmpty(),
                participantCount = participants.size,
                participants = participants.map { it.toParticipantAvatarUiState() },
                isMicrophoneEnabled = isMicEnabled,
                modifier = Modifier.padding(bottom = 45.dp), // Now 92dp tall, will be covered by 92dp bottom bar
                onRoomClick = {
                    activeRoomId?.let { roomId ->
                        presentationStore.expand(roomId)
                        navController.navigate(OnboardingRoute.RoomDetailRoute(roomId = roomId, asHost = false))
                    }
                },
                onSendClick = {
                    scope.launch {
                        roomCallDataSource.setMicrophoneEnabled(!isMicEnabled)
                    }
                }
            ) {
                    activeRoomId?.let { roomId ->
                        presentationStore.expand(roomId)
                        navController.navigate(OnboardingRoute.RoomDetailRoute(roomId = roomId, asHost = false))
                    }
                }
            }
        }
    }
}
