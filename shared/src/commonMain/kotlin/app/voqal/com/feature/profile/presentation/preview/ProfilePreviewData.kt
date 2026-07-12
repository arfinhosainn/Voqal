package app.voqal.com.feature.profile.presentation.preview

import app.voqal.com.feature.profile.presentation.model.LanguageUi
import app.voqal.com.feature.profile.presentation.model.ProfileActions
import app.voqal.com.feature.profile.presentation.model.ProfileUi
import app.voqal.com.feature.profile.presentation.model.SocialLinkUi
import app.voqal.com.feature.profile.presentation.model.UpcomingRoomUi

object ProfilePreviewData {
    val profile = ProfileUi(
        id = "1",
        name = "Xing Zheng",
        role = "Designer",
        avatarUrl = null,
        followersCount = 2200,
        followingCount = 103,
        bio = "Video Editor + AV Engineer. Building communities around audio experiences and creative technology. Passionate about design systems and accessibility.",
        isFollowing = false,
        socialLinks = listOf(
            SocialLinkUi(label = "Instagram", value = null),
            SocialLinkUi(label = "WhatsApp", value = "Chat"),
            SocialLinkUi(label = "Website", value = "xingzheng.com"),
        ),
        interests = listOf("Design", "Audio", "Tech", "Creator", "Gaming"),
        languages = listOf(
            LanguageUi(code = "\uD83C\uDDFA\uD83C\uDDF8", name = "English"),
            LanguageUi(code = "\uD83C\uDDE7\uD83C\uDDE9", name = "Bangla"),
            LanguageUi(code = "\uD83C\uDDF2\uD83C\uDDFE", name = "Malay"),
        ),
        communities = listOf("Design Hub", "Kotlin Devs", "Music Lovers"),
        upcomingRoom = UpcomingRoomUi(
            title = "AI & Future of Audio Apps",
            timeDescription = "Tomorrow • 8 PM",
        ),
        recentlyJoinedRooms = listOf(
            "UX Review Session",
            "English Practice",
            "Startup Talk",
        ),
    )

    val actions = ProfileActions(
        onDismiss = {},
        onFollow = {},
        onSettingsClick = {},
    )
}
