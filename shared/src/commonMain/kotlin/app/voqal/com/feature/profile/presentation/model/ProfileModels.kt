package app.voqal.com.feature.profile.presentation.model

data class ProfileUi(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String?,
    val followersCount: Int,
    val followingCount: Int,
    val bio: String,
    val isFollowing: Boolean = false,
    val socialLinks: List<SocialLinkUi> = emptyList(),
    val interests: List<String> = emptyList(),
    val languages: List<LanguageUi> = emptyList(),
    val communities: List<String> = emptyList(),
    val upcomingRoom: UpcomingRoomUi? = null,
    val recentlyJoinedRooms: List<String> = emptyList(),
)

data class SocialLinkUi(
    val label: String,
    val value: String?,
)

data class LanguageUi(
    val code: String,
    val name: String,
)

data class UpcomingRoomUi(
    val title: String,
    val timeDescription: String,
)

data class ProfileActions(
    val onDismiss: () -> Unit,
    val onFollow: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onAddSocialLink: () -> Unit = {},
)
