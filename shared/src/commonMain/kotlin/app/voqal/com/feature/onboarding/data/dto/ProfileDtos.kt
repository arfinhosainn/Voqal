package app.voqal.com.feature.onboarding.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpsertDto(
    val id: String,
    val email: String,
    @SerialName("onboarding_step")
    val onboardingStep: Int = 2
)

@Serializable
data class ProfileIdDto(
    val id: String
)

@Serializable
data class ProfileUpdateDto(
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    @SerialName("primary_language_code")
    val primaryLanguageCode: String? = null,
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("onboarding_step")
    val onboardingStep: Int? = null,
    // New profile detail fields
    val bio: String? = null,
    val occupation: String? = null,
    val website: String? = null,
    val birthday: String? = null, // ISO date string
    @SerialName("allow_following")
    val allowFollowing: Boolean? = null,
    @SerialName("allow_dm")
    val allowDm: Boolean? = null,
    @SerialName("show_online")
    val showOnline: Boolean? = null
)

@Serializable
data class CompleteOnboardingParams(
    @SerialName("p_interest_ids")
    val interestIds: List<String>
)

@Serializable
data class SocialLinkUpsertDto(
    @SerialName("user_id")
    val userId: String,
    val platform: String,
    val url: String,
    val username: String? = null,
    val visibility: String = "public",
    val verified: Boolean = false
)

@Serializable
data class SocialLinkDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val platform: String,
    val url: String,
    val username: String?,
    val visibility: String,
    val verified: Boolean,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class InterestDto(
    val id: String,
    val name: String,
    val emoji: String?,
    val category: String
)

@Serializable
data class InterestCategoryDto(
    val id: String,
    val name: String,
    @SerialName("display_order")
    val displayOrder: Int
)

// Follow DTOs
@Serializable
data class FollowUpsertDto(
    @SerialName("follower_id")
    val followerId: String,
    @SerialName("following_id")
    val followingId: String
)

@Serializable
data class FollowDto(
    @SerialName("follower_id")
    val followerId: String,
    @SerialName("following_id")
    val followingId: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class FollowCountDto(
    @SerialName("count")
    val count: Int
)

// Profile Stats DTO
@Serializable
data class ProfileStatsDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("following_count")
    val followingCount: Int,
    @SerialName("rooms_joined")
    val roomsJoined: Int,
    @SerialName("rooms_hosted")
    val roomsHosted: Int,
    @SerialName("rooms_created")
    val roomsCreated: Int,
    @SerialName("likes_received")
    val likesReceived: Int,
    @SerialName("last_updated")
    val lastUpdated: String
)

// Room DTOs for Phase 3
@Serializable
data class RoomUpsertDto(
    val id: String,
    val title: String,
    val category: String,
    @SerialName("scheduled_for")
    val scheduledFor: String? = null,
    val status: String = "scheduled",
    val visibility: String = "public",
    val type: String = "voice"
)

@Serializable
data class RoomDto(
    val id: String,
    @SerialName("host_id")
    val hostId: String,
    val title: String,
    val category: String,
    val visibility: String,
    val status: String,
    val type: String,
    @SerialName("scheduled_for")
    val scheduledFor: String?,
    @SerialName("listener_count")
    val listenerCount: Int,
    @SerialName("comment_count")
    val commentCount: Int,
    @SerialName("participant_preview")
    val participantPreview: String,
    @SerialName("last_activity_at")
    val lastActivityAt: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class RoomMemberDto(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("user_id")
    val userId: String,
    val role: String,
    @SerialName("joined_at")
    val joinedAt: String,
    @SerialName("left_at")
    val leftAt: String?,
    @SerialName("hand_raised")
    val handRaised: Boolean
)

@Serializable
data class RoomHistoryDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("room_id")
    val roomId: String,
    val role: String,
    @SerialName("joined_at")
    val joinedAt: String,
    @SerialName("left_at")
    val leftAt: String?,
    val duration: String?
)

@Serializable
data class RoomInviteDto(
    val id: String,
    @SerialName("room_id")
    val roomId: String,
    @SerialName("from_user_id")
    val fromUserId: String,
    @SerialName("to_user_id")
    val toUserId: String,
    val status: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class RoomInviteUpsertDto(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("from_user_id")
    val fromUserId: String,
    @SerialName("to_user_id")
    val toUserId: String,
    val status: String = "pending"
)

@Serializable
data class RoomReminderDto(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class RoomReminderUpsertDto(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("user_id")
    val userId: String
)

@Serializable
data class SavedRoomDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("room_id")
    val roomId: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class SavedRoomUpsertDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("room_id")
    val roomId: String
)

// Phase 4: Safety & Engagement DTOs
@Serializable
data class UserBlockDto(
    @SerialName("blocker_id")
    val blockerId: String,
    @SerialName("blocked_id")
    val blockedId: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class UserBlockUpsertDto(
    @SerialName("blocker_id")
    val blockerId: String,
    @SerialName("blocked_id")
    val blockedId: String
)

@Serializable
data class UserMuteDto(
    @SerialName("muter_id")
    val muterId: String,
    @SerialName("muted_id")
    val mutedId: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class UserMuteUpsertDto(
    @SerialName("muter_id")
    val muterId: String,
    @SerialName("muted_id")
    val mutedId: String
)

@Serializable
data class UserReportDto(
    val id: String,
    @SerialName("reporter_id")
    val reporterId: String,
    @SerialName("reported_user_id")
    val reportedUserId: String,
    val reason: String,
    val description: String?,
    val status: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("reviewed_at")
    val reviewedAt: String?,
    @SerialName("reviewed_by")
    val reviewedBy: String?
)

@Serializable
data class UserReportUpsertDto(
    @SerialName("reporter_id")
    val reporterId: String,
    @SerialName("reported_user_id")
    val reportedUserId: String,
    val reason: String,
    val description: String? = null
)

@Serializable
data class NotificationDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("actor_id")
    val actorId: String?,
    val type: String,
    @SerialName("entity_id")
    val entityId: String?,
    @SerialName("entity_type")
    val entityType: String?,
    val title: String,
    val body: String?,
    @SerialName("read_at")
    val readAt: String?,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class NotificationUpsertDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("actor_id")
    val actorId: String?,
    val type: String,
    @SerialName("entity_id")
    val entityId: String?,
    @SerialName("entity_type")
    val entityType: String?,
    val title: String,
    val body: String? = null
)

@Serializable
data class UserPresenceDto(
    @SerialName("user_id")
    val userId: String,
    val status: String,
    @SerialName("current_room_id")
    val currentRoomId: String?,
    @SerialName("last_seen_at")
    val lastSeenAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class UserPresenceUpsertDto(
    @SerialName("user_id")
    val userId: String,
    val status: String,
    @SerialName("current_room_id")
    val currentRoomId: String? = null
)
