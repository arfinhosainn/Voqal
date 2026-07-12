package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.profile.presentation.model.ProfileActions
import app.voqal.com.feature.profile.presentation.model.ProfileUi

@Composable
fun ProfileContent(
    profile: ProfileUi,
    actions: ProfileActions,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = VoqalTheme.colors.surface,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ProfileTopBar(onSettingsClick = actions.onSettingsClick)

            Spacer(modifier = Modifier.height(20.dp))

            ProfileHeader(profile = profile, onFollowClick = actions.onFollow)

            Spacer(modifier = Modifier.height(20.dp))

            ProfileStats(profile = profile)

            Spacer(modifier = Modifier.height(20.dp))
            ProfileSection(title = "Social Links", showDivider = true) {
                ProfileSocialLinks(
                    links = profile.socialLinks,
                    onLinkClick = {},
                    onAddClick = actions.onAddSocialLink,
                )
            }

            ProfileSection(title = "Bio") {
                ProfileBio(text = profile.bio)
            }

            if (profile.interests.isNotEmpty()) {
                ProfileSection(title = "Interests") {
                    ProfileInterestSection(interests = profile.interests)
                }
            }

            if (profile.languages.isNotEmpty()) {
                ProfileSection(title = "Languages") {
                    ProfileLanguageSection(languages = profile.languages)
                }
            }

            if (profile.communities.isNotEmpty()) {
                ProfileSection(title = "Communities") {
                    ProfileCommunitySection(communities = profile.communities)
                }
            }

            if (profile.upcomingRoom != null) {
                ProfileSection(title = "Upcoming Room") {
                    ProfileUpcomingRoom(room = profile.upcomingRoom)
                }
            }

            if (profile.recentlyJoinedRooms.isNotEmpty()) {
                ProfileSection(title = "Recently Joined") {
                    ProfileRecentRooms(rooms = profile.recentlyJoinedRooms)
                }
            }
        }
    }
}
