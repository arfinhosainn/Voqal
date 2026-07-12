package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.data.dto.SocialLinkDto
import app.voqal.com.feature.onboarding.data.dto.InterestDto
import app.voqal.com.feature.onboarding.data.dto.InterestCategoryDto
import app.voqal.com.feature.onboarding.data.dto.FollowDto
import app.voqal.com.feature.onboarding.data.dto.ProfileStatsDto
import app.voqal.com.feature.onboarding.data.dto.RoomDto
import app.voqal.com.feature.onboarding.data.dto.RoomMemberDto
import app.voqal.com.feature.onboarding.data.dto.RoomInviteDto
import app.voqal.com.feature.onboarding.data.dto.RoomReminderDto
import app.voqal.com.feature.onboarding.data.dto.UserBlockDto
import app.voqal.com.feature.onboarding.data.dto.UserMuteDto
import app.voqal.com.feature.onboarding.data.dto.UserReportDto
import app.voqal.com.feature.onboarding.data.dto.NotificationDto
import app.voqal.com.feature.onboarding.data.dto.UserPresenceDto

interface OnboardingProfileDataSource {
    suspend fun getOnboardingStep(): Result<Int?, OnboardingProfileError>
    suspend fun ensureProfileExists(): EmptyResult<OnboardingProfileError>
    suspend fun updateFullName(firstName: String, lastName: String): EmptyResult<OnboardingProfileError>
    suspend fun isUsernameAvailable(username: String): Result<Boolean, OnboardingProfileError>
    suspend fun updateUsername(username: String): EmptyResult<OnboardingProfileError>
    suspend fun uploadAvatar(photoBytes: ByteArray): EmptyResult<OnboardingProfileError>
    suspend fun updateLanguage(languageCode: String): EmptyResult<OnboardingProfileError>
    suspend fun completeOnboarding(interestIds: Set<String>): EmptyResult<OnboardingProfileError>
    
    // New profile detail methods
    suspend fun updateProfileDetails(
        bio: String?,
        occupation: String?,
        website: String?,
        birthday: String?, // ISO date string
        allowFollowing: Boolean?,
        allowDm: Boolean?,
        showOnline: Boolean?
    ): EmptyResult<OnboardingProfileError>
    
    // Social links
    suspend fun getSocialLinks(): Result<List<SocialLinkDto>, OnboardingProfileError>
    suspend fun addSocialLink(platform: String, url: String, username: String?, visibility: String): EmptyResult<OnboardingProfileError>
    suspend fun updateSocialLink(linkId: String, url: String, username: String?, visibility: String): EmptyResult<OnboardingProfileError>
    suspend fun deleteSocialLink(linkId: String): EmptyResult<OnboardingProfileError>
    
    // Interests
    suspend fun getInterests(): Result<List<InterestDto>, OnboardingProfileError>
    suspend fun getInterestCategories(): Result<List<InterestCategoryDto>, OnboardingProfileError>
    suspend fun updateInterests(interestIds: Set<String>): EmptyResult<OnboardingProfileError>
    
    // Follows
    suspend fun followUser(targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun unfollowUser(targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun getFollowers(userId: String, limit: Int, offset: Int): Result<List<FollowDto>, OnboardingProfileError>
    suspend fun getFollowing(userId: String, limit: Int, offset: Int): Result<List<FollowDto>, OnboardingProfileError>
    suspend fun isFollowing(targetUserId: String): Result<Boolean, OnboardingProfileError>
    suspend fun getFollowerCount(userId: String): Result<Int, OnboardingProfileError>
    suspend fun getFollowingCount(userId: String): Result<Int, OnboardingProfileError>
    
    // Profile stats
    suspend fun getProfileStats(userId: String): Result<ProfileStatsDto, OnboardingProfileError>
    
    // Room methods
    suspend fun createRoom(title: String, category: String, scheduledFor: String?, visibility: String): Result<RoomDto, OnboardingProfileError>
    suspend fun getRoom(roomId: String): Result<RoomDto, OnboardingProfileError>
    suspend fun getScheduledRooms(userId: String, limit: Int, offset: Int): Result<List<RoomDto>, OnboardingProfileError>
    suspend fun updateRoomStatus(roomId: String, status: String): EmptyResult<OnboardingProfileError>
    suspend fun endRoom(roomId: String): EmptyResult<OnboardingProfileError>
    
    // Room members
    suspend fun getRoomMembers(roomId: String): Result<List<RoomMemberDto>, OnboardingProfileError>
    suspend fun raiseHand(roomId: String): EmptyResult<OnboardingProfileError>
    suspend fun lowerHand(roomId: String): EmptyResult<OnboardingProfileError>
    suspend fun updateMemberRole(roomId: String, userId: String, role: String): EmptyResult<OnboardingProfileError>
    
    // Room invites
    suspend fun inviteToRoom(roomId: String, targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun getRoomInvites(userId: String): Result<List<RoomDto>, OnboardingProfileError>
    suspend fun acceptInvite(inviteId: String): EmptyResult<OnboardingProfileError>
    suspend fun declineInvite(inviteId: String): EmptyResult<OnboardingProfileError>
    
    // Room reminders
    suspend fun setRoomReminder(roomId: String): EmptyResult<OnboardingProfileError>
    suspend fun removeRoomReminder(roomId: String): EmptyResult<OnboardingProfileError>
    suspend fun getRoomReminders(userId: String): Result<List<RoomDto>, OnboardingProfileError>
    
    // Room history
    suspend fun getUserRoomHistory(userId: String, limit: Int, offset: Int): Result<List<RoomDto>, OnboardingProfileError>
    
    // Phase 4: Blocks
    suspend fun blockUser(targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun unblockUser(targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun getBlockedUsers(userId: String, limit: Int, offset: Int): Result<List<UserBlockDto>, OnboardingProfileError>
    suspend fun isBlocked(targetUserId: String): Result<Boolean, OnboardingProfileError>
    
    // Phase 4: Mutes
    suspend fun muteUser(targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun unmuteUser(targetUserId: String): EmptyResult<OnboardingProfileError>
    suspend fun getMutedUsers(userId: String, limit: Int, offset: Int): Result<List<UserMuteDto>, OnboardingProfileError>
    suspend fun isMuted(targetUserId: String): Result<Boolean, OnboardingProfileError>
    
    // Phase 4: Reports
    suspend fun reportUser(targetUserId: String, reason: String, description: String?): EmptyResult<OnboardingProfileError>
    suspend fun getUserReports(userId: String, limit: Int, offset: Int): Result<List<UserReportDto>, OnboardingProfileError>
    
    // Phase 4: Notifications
    suspend fun getNotifications(userId: String, limit: Int, offset: Int): Result<List<NotificationDto>, OnboardingProfileError>
    suspend fun markNotificationRead(notificationId: String): EmptyResult<OnboardingProfileError>
    suspend fun markAllNotificationsRead(userId: String): EmptyResult<OnboardingProfileError>
    suspend fun getUnreadNotificationCount(userId: String): Result<Int, OnboardingProfileError>
    
    // Phase 4: Presence
    suspend fun updatePresence(status: String, currentRoomId: String?): EmptyResult<OnboardingProfileError>
    suspend fun getPresence(userId: String): Result<UserPresenceDto, OnboardingProfileError>
    suspend fun getUserPresences(userIds: List<String>): Result<List<UserPresenceDto>, OnboardingProfileError>
}
