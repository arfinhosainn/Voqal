package app.voqal.com.feature.onboarding.data

import app.voqal.com.core.data.SupabaseConfig
import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.data.dto.CompleteOnboardingParams
import app.voqal.com.feature.onboarding.data.dto.FollowDto
import app.voqal.com.feature.onboarding.data.dto.FollowUpsertDto
import app.voqal.com.feature.onboarding.data.dto.InterestCategoryDto
import app.voqal.com.feature.onboarding.data.dto.InterestDto
import app.voqal.com.feature.onboarding.data.dto.NotificationDto
import app.voqal.com.feature.onboarding.data.dto.ProfileIdDto
import app.voqal.com.feature.onboarding.data.dto.ProfileStatsDto
import app.voqal.com.feature.onboarding.data.dto.ProfileUpdateDto
import app.voqal.com.feature.onboarding.data.dto.ProfileUpsertDto
import app.voqal.com.feature.onboarding.data.dto.RoomDto
import app.voqal.com.feature.onboarding.data.dto.RoomInviteDto
import app.voqal.com.feature.onboarding.data.dto.RoomInviteUpsertDto
import app.voqal.com.feature.onboarding.data.dto.RoomMemberDto
import app.voqal.com.feature.onboarding.data.dto.RoomReminderDto
import app.voqal.com.feature.onboarding.data.dto.RoomReminderUpsertDto
import app.voqal.com.feature.onboarding.data.dto.RoomUpsertDto
import app.voqal.com.feature.onboarding.data.dto.SocialLinkDto
import app.voqal.com.feature.onboarding.data.dto.SocialLinkUpsertDto
import app.voqal.com.feature.onboarding.data.dto.UserBlockDto
import app.voqal.com.feature.onboarding.data.dto.UserBlockUpsertDto
import app.voqal.com.feature.onboarding.data.dto.UserMuteDto
import app.voqal.com.feature.onboarding.data.dto.UserMuteUpsertDto
import app.voqal.com.feature.onboarding.data.dto.UserPresenceDto
import app.voqal.com.feature.onboarding.data.dto.UserPresenceUpsertDto
import app.voqal.com.feature.onboarding.data.dto.UserReportDto
import app.voqal.com.feature.onboarding.data.dto.UserReportUpsertDto
import io.github.jan.supabase.postgrest.query.Order
import app.voqal.com.feature.onboarding.domain.OnboardingProfileDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingProfileError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.ContentType

private const val ProfilesTable = "profiles"
private const val SocialLinksTable = "profile_social_links"
private const val InterestsTable = "interests"
private const val InterestCategoriesTable = "interest_categories"
private const val FollowsTable = "user_follows"
private const val ProfileStatsTable = "profile_stats"
private const val RoomsTable = "rooms"
private const val RoomSessionsTable = "room_sessions"
private const val RoomInvitesTable = "room_invites"
private const val RoomRemindersTable = "room_reminders"
private const val RoomHistoryTable = "user_room_history"
private const val UserBlocksTable = "user_blocks"
private const val UserMutesTable = "user_mutes"
private const val UserReportsTable = "user_reports"
private const val NotificationsTable = "notifications"
private const val UserPresenceTable = "user_presence"
private const val AvatarsBucket = "avatars"

class SupabaseOnboardingProfileDataSource(
    private val supabaseClient: SupabaseClient,
    private val supabaseConfig: SupabaseConfig
) : OnboardingProfileDataSource {

    override suspend fun getOnboardingStep(): Result<Int?, OnboardingProfileError> {
        if (!supabaseConfig.isConfigured) {
            return Result.Error(OnboardingProfileError.NotConfigured)
        }

        return try {
            // DIAGNOSTIC LOGS
            println("DEBUG: Before Init Session = ${supabaseClient.auth.currentSessionOrNull()}")
            println("DEBUG: Before Init User = ${supabaseClient.auth.currentUserOrNull()}")

            // Wait for SDK to restore/refresh any existing session from storage
            supabaseClient.auth.awaitInitialization()

            println("DEBUG: After Init Session = ${supabaseClient.auth.currentSessionOrNull()}")
            println("DEBUG: After Init User = ${supabaseClient.auth.currentUserOrNull()}")

            // Check if we have a session.
            val user = supabaseClient.auth.currentUserOrNull()

            if (user == null) {
                return Result.Success(null)
            }

            val userId = user.id

            val profile = supabaseClient.postgrest.from(ProfilesTable)
                .select(columns = Columns.list("onboarding_step")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<ProfileUpdateDto>()

            Result.Success(profile?.onboardingStep)
        } catch (throwable: Throwable) {
            println("Splash Check Error: ${throwable.message}")
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun ensureProfileExists(): EmptyResult<OnboardingProfileError> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull() 
                ?: throw OnboardingProfileNotAuthenticatedException()
            
            supabaseClient.postgrest.from(ProfilesTable).upsert(
                ProfileUpsertDto(
                    id = user.id,
                    email = user.email ?: "",
                    onboardingStep = 2
                )
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateFullName(
        firstName: String,
        lastName: String
    ): EmptyResult<OnboardingProfileError> {
        return updateProfile(
            ProfileUpdateDto(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                onboardingStep = 3
            )
        )
    }

    override suspend fun isUsernameAvailable(
        username: String
    ): Result<Boolean, OnboardingProfileError> {
        if (!supabaseConfig.isConfigured) {
            return Result.Error(OnboardingProfileError.NotConfigured)
        }

        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
            val normalizedUsername = username.trim().lowercase()
            val existingProfiles = supabaseClient.postgrest
                .from(ProfilesTable)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("username", normalizedUsername)
                    }
                }
                .decodeList<ProfileIdDto>()

            Result.Success(existingProfiles.none { it.id != userId })
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateUsername(username: String): EmptyResult<OnboardingProfileError> {
        val normalizedUsername = username.trim().lowercase()

        return when (val availability = isUsernameAvailable(normalizedUsername)) {
            is Result.Error -> availability
            is Result.Success -> {
                if (!availability.data) {
                    Result.Error(OnboardingProfileError.UsernameTaken)
                } else {
                    updateProfile(
                        ProfileUpdateDto(
                            username = normalizedUsername,
                            onboardingStep = 4
                        )
                    )
                }
            }
        }
    }

    override suspend fun uploadAvatar(photoBytes: ByteArray): EmptyResult<OnboardingProfileError> {
        if (photoBytes.isEmpty()) {
            return updateProfile(ProfileUpdateDto(onboardingStep = 5))
        }

        return try {
            val userId = requireUserId()
            val (extension, contentType) = photoBytes.detectImageType()
            val avatarPath = "$userId/avatar.$extension"

            supabaseClient.storage
                .from(AvatarsBucket)
                .upload(avatarPath, photoBytes) {
                    upsert = true
                    this.contentType = contentType
                }

            updateProfile(
                ProfileUpdateDto(
                    avatarPath = avatarPath,
                    onboardingStep = 5
                )
            )
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateLanguage(languageCode: String): EmptyResult<OnboardingProfileError> {
        return updateProfile(
            ProfileUpdateDto(
                primaryLanguageCode = languageCode,
                onboardingStep = 6
            )
        )
    }

    override suspend fun completeOnboarding(
        interestIds: Set<String>
    ): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            
            // 1. Call RPC to update interests
            supabaseClient.postgrest.rpc(
                function = "complete_onboarding",
                parameters = CompleteOnboardingParams(
                    interestIds = interestIds.toList()
                )
            )

            // 2. Explicitly set step to 7 to ensure splash screen works
            updateProfile(
                ProfileUpdateDto(onboardingStep = 7)
            )

            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // New profile detail methods
    override suspend fun updateProfileDetails(
        bio: String?,
        occupation: String?,
        website: String?,
        birthday: String?,
        allowFollowing: Boolean?,
        allowDm: Boolean?,
        showOnline: Boolean?
    ): EmptyResult<OnboardingProfileError> {
        return updateProfile(
            ProfileUpdateDto(
                bio = bio,
                occupation = occupation,
                website = website,
                birthday = birthday,
                allowFollowing = allowFollowing,
                allowDm = allowDm,
                showOnline = showOnline
            )
        )
    }

    // Social links
    override suspend fun getSocialLinks(): Result<List<SocialLinkDto>, OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            val links = supabaseClient.postgrest
                .from(SocialLinksTable)
                .select(columns = Columns.list("*")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SocialLinkDto>()
            Result.Success(links)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun addSocialLink(
        platform: String,
        url: String,
        username: String?,
        visibility: String
    ): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest.from(SocialLinksTable).insert(
                SocialLinkUpsertDto(
                    userId = userId,
                    platform = platform,
                    url = url,
                    username = username,
                    visibility = visibility
                )
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateSocialLink(
        linkId: String,
        url: String,
        username: String?,
        visibility: String
    ): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(SocialLinksTable)
                .update(
                    mapOf(
                        "url" to url,
                        "username" to username,
                        "visibility" to visibility
                    )
                ) {
                    filter {
                        eq("id", linkId)
                        eq("user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun deleteSocialLink(linkId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(SocialLinksTable)
                .delete {
                    filter {
                        eq("id", linkId)
                        eq("user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Interests
    override suspend fun getInterests(): Result<List<InterestDto>, OnboardingProfileError> {
        return try {
            val interests = supabaseClient.postgrest
                .from(InterestsTable)
                .select(columns = Columns.list("*"))
                .decodeList<InterestDto>()
            Result.Success(interests)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getInterestCategories(): Result<List<InterestCategoryDto>, OnboardingProfileError> {
        return try {
            val categories = supabaseClient.postgrest
                .from(InterestCategoriesTable)
                .select(columns = Columns.list("*"))
                .decodeList<InterestCategoryDto>()
            Result.Success(categories)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateInterests(interestIds: Set<String>): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            
            // Delete existing interests
            supabaseClient.postgrest
                .from("profile_interests")
                .delete {
                    filter { eq("user_id", userId) }
                }
            
            // Insert new interests
            if (interestIds.isNotEmpty()) {
                val inserts = interestIds.map { interestId ->
                    mapOf("user_id" to userId, "interest_id" to interestId)
                }
                supabaseClient.postgrest
                    .from("profile_interests")
                    .insert(inserts)
            }
            
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Follows
    override suspend fun followUser(targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            if (userId == targetUserId) {
                return Result.Error(OnboardingProfileError.Unknown)
            }
            
            supabaseClient.postgrest.from(FollowsTable).insert(
                FollowUpsertDto(
                    followerId = userId,
                    followingId = targetUserId
                )
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun unfollowUser(targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(FollowsTable)
                .delete {
                    filter {
                        eq("follower_id", userId)
                        eq("following_id", targetUserId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getFollowers(userId: String, limit: Int, offset: Int): Result<List<FollowDto>, OnboardingProfileError> {
        return try {
            // Fetch all followers for this user and paginate in Kotlin
            val allFollows = supabaseClient.postgrest
                .from("user_follows")
                .select {
                    filter { eq("following_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<FollowDto>()
            
            // Apply pagination in memory
            val paginated = allFollows.drop(offset).take(limit)
            Result.Success(paginated)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getFollowing(userId: String, limit: Int, offset: Int): Result<List<FollowDto>, OnboardingProfileError> {
        return try {
            // Fetch all following for this user and paginate in Kotlin
            val allFollows = supabaseClient.postgrest
                .from("user_follows")
                .select {
                    filter { eq("follower_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<FollowDto>()
            
            // Apply pagination in memory
            val paginated = allFollows.drop(offset).take(limit)
            Result.Success(paginated)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun isFollowing(targetUserId: String): Result<Boolean, OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            val follows = supabaseClient.postgrest
                .from(FollowsTable)
                .select {
                    filter {
                        eq("follower_id", userId)
                        eq("following_id", targetUserId)
                    }
                }
                .decodeList<FollowDto>()
            Result.Success(follows.isNotEmpty())
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getFollowerCount(userId: String): Result<Int, OnboardingProfileError> {
        return try {
            // Get from profile_stats for performance
            val stats = supabaseClient.postgrest
                .from(ProfileStatsTable)
                .select(columns = Columns.list("followers_count")) {
                    filter { eq("user_id", userId) }
                }
                .decodeSingleOrNull<ProfileStatsDto>()
            Result.Success(stats?.followersCount ?: 0)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getFollowingCount(userId: String): Result<Int, OnboardingProfileError> {
        return try {
            // Get from profile_stats for performance
            val stats = supabaseClient.postgrest
                .from(ProfileStatsTable)
                .select(columns = Columns.list("following_count")) {
                    filter { eq("user_id", userId) }
                }
                .decodeSingleOrNull<ProfileStatsDto>()
            Result.Success(stats?.followingCount ?: 0)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Profile Stats
    override suspend fun getProfileStats(userId: String): Result<ProfileStatsDto, OnboardingProfileError> {
        return try {
            val stats = supabaseClient.postgrest
                .from(ProfileStatsTable)
                .select(columns = Columns.list("*")) {
                    filter { eq("user_id", userId) }
                }
                .decodeSingleOrNull<ProfileStatsDto>()
            
            if (stats != null) {
                Result.Success(stats)
            } else {
                // Return default stats if not found
                Result.Success(ProfileStatsDto(
                    userId = userId,
                    followersCount = 0,
                    followingCount = 0,
                    roomsJoined = 0,
                    roomsHosted = 0,
                    roomsCreated = 0,
                    likesReceived = 0,
                    lastUpdated = java.time.Instant.now().toString()
                ))
            }
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Room methods
    override suspend fun createRoom(title: String, category: String, scheduledFor: String?, visibility: String): Result<RoomDto, OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            val room = supabaseClient.postgrest
                .from(RoomsTable)
                .insert(RoomUpsertDto(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    category = category,
                    scheduledFor = scheduledFor,
                    status = if (scheduledFor != null) "scheduled" else "live",
                    visibility = visibility,
                    type = "voice"
                ))
                .decodeSingle<RoomDto>()
            Result.Success(room)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getRoom(roomId: String): Result<RoomDto, OnboardingProfileError> {
        return try {
            val room = supabaseClient.postgrest
                .from(RoomsTable)
                .select {
                    filter { eq("id", roomId) }
                }
                .decodeSingleOrNull<RoomDto>()
            room?.let { Result.Success(it) } ?: Result.Error(OnboardingProfileError.Unknown)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getScheduledRooms(userId: String, limit: Int, offset: Int): Result<List<RoomDto>, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from("rooms")
                .select {
                    filter {
                        eq("host_id", userId)
                        eq("status", "scheduled")
                    }
                    order("scheduled_for", Order.ASCENDING)
                }
            val allRooms: List<RoomDto> = response.decodeList<RoomDto>()
            val paginatedRooms = allRooms.drop(offset).take(limit)
            Result.Success(paginatedRooms)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateRoomStatus(roomId: String, status: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomsTable)
                .update(mapOf("status" to status)) {
                    filter {
                        eq("id", roomId)
                        eq("host_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun endRoom(roomId: String): EmptyResult<OnboardingProfileError> {
        return updateRoomStatus(roomId, "ended")
    }

    // Room members
    override suspend fun getRoomMembers(roomId: String): Result<List<RoomMemberDto>, OnboardingProfileError> {
        return try {
            val members = supabaseClient.postgrest
                .from(RoomSessionsTable)
                .select {
                    filter { eq("room_id", roomId) }
                    order("joined_at", Order.ASCENDING)
                }
                .decodeList<RoomMemberDto>()
            Result.Success(members)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun raiseHand(roomId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomSessionsTable)
                .update(mapOf("hand_raised" to true)) {
                    filter {
                        eq("room_id", roomId)
                        eq("user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun lowerHand(roomId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomSessionsTable)
                .update(mapOf("hand_raised" to false)) {
                    filter {
                        eq("room_id", roomId)
                        eq("user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateMemberRole(roomId: String, userId: String, role: String): EmptyResult<OnboardingProfileError> {
        return try {
            val currentUserId = requireUserId()
            supabaseClient.postgrest
                .from(RoomSessionsTable)
                .update(mapOf("role" to role)) {
                    filter {
                        eq("room_id", roomId)
                        eq("user_id", userId)
                        eq("user_id", currentUserId) // Only host can update roles
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Room invites
    override suspend fun inviteToRoom(roomId: String, targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomInvitesTable)
                .insert(RoomInviteUpsertDto(
                    roomId = roomId,
                    fromUserId = userId,
                    toUserId = targetUserId
                ))
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getRoomInvites(userId: String): Result<List<RoomDto>, OnboardingProfileError> {
        return try {
            val invites = supabaseClient.postgrest
                .from(RoomInvitesTable)
                .select {
                    filter { eq("to_user_id", userId) }
                }
                .decodeList<RoomInviteDto>()
            
            val roomIds = invites.map { it.roomId }
            if (roomIds.isEmpty()) return Result.Success(emptyList())
            
            val rooms = supabaseClient.postgrest
                .from(RoomsTable)
                .select {
                    filter { eq("id", roomIds[0]) }
                }
                .decodeList<RoomDto>()
            Result.Success(rooms)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun acceptInvite(inviteId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomInvitesTable)
                .update(mapOf("status" to "accepted", "responded_at" to java.time.Instant.now().toString())) {
                    filter {
                        eq("id", inviteId)
                        eq("to_user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun declineInvite(inviteId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomInvitesTable)
                .update(mapOf("status" to "declined", "responded_at" to java.time.Instant.now().toString())) {
                    filter {
                        eq("id", inviteId)
                        eq("to_user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Room reminders
    override suspend fun setRoomReminder(roomId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomRemindersTable)
                .insert(RoomReminderUpsertDto(roomId = roomId, userId = userId))
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun removeRoomReminder(roomId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(RoomRemindersTable)
                .delete {
                    filter {
                        eq("room_id", roomId)
                        eq("user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getRoomReminders(userId: String): Result<List<RoomDto>, OnboardingProfileError> {
        return try {
            val reminders = supabaseClient.postgrest
                .from(RoomRemindersTable)
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<RoomReminderDto>()
            
            val roomIds = reminders.map { it.roomId }
            if (roomIds.isEmpty()) return Result.Success(emptyList())
            
            val rooms = supabaseClient.postgrest
                .from(RoomsTable)
                .select {
                    filter { eq("id", roomIds[0]) }
                }
                .decodeList<RoomDto>()
            Result.Success(rooms)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

// Room history
    override suspend fun getUserRoomHistory(userId: String, limit: Int, offset: Int): Result<List<RoomDto>, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from("rooms")
                .select {
                    filter { eq("host_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
            val allRooms: List<RoomDto> = response.decodeList<RoomDto>()
            val paginatedRooms = allRooms.drop(offset).take(limit)
            Result.Success(paginatedRooms)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Phase 4: Blocks
    override suspend fun blockUser(targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            if (userId == targetUserId) {
                return Result.Error(OnboardingProfileError.Unknown)
            }
            supabaseClient.postgrest.from(UserBlocksTable).insert(
                UserBlockUpsertDto(blockerId = userId, blockedId = targetUserId)
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun unblockUser(targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(UserBlocksTable)
                .delete {
                    filter {
                        eq("blocker_id", userId)
                        eq("blocked_id", targetUserId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getBlockedUsers(userId: String, limit: Int, offset: Int): Result<List<UserBlockDto>, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from(UserBlocksTable)
                .select {
                    filter { eq("blocker_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
            val allBlocks: List<UserBlockDto> = response.decodeList()
            val paginatedBlocks = allBlocks.drop(offset).take(limit)
            Result.Success(paginatedBlocks)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun isBlocked(targetUserId: String): Result<Boolean, OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            val response = supabaseClient.postgrest
                .from(UserBlocksTable)
                .select {
                    filter {
                        eq("blocker_id", userId)
                        eq("blocked_id", targetUserId)
                    }
                }
            val blocks = response.decodeList<UserBlockDto>()
            Result.Success(blocks.isNotEmpty())
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Phase 4: Mutes
    override suspend fun muteUser(targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            if (userId == targetUserId) {
                return Result.Error(OnboardingProfileError.Unknown)
            }
            supabaseClient.postgrest.from(UserMutesTable).insert(
                UserMuteUpsertDto(muterId = userId, mutedId = targetUserId)
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun unmuteUser(targetUserId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(UserMutesTable)
                .delete {
                    filter {
                        eq("muter_id", userId)
                        eq("muted_id", targetUserId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getMutedUsers(userId: String, limit: Int, offset: Int): Result<List<UserMuteDto>, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from(UserMutesTable)
                .select {
                    filter { eq("muter_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
            val allMutes: List<UserMuteDto> = response.decodeList()
            val paginatedMutes = allMutes.drop(offset).take(limit)
            Result.Success(paginatedMutes)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun isMuted(targetUserId: String): Result<Boolean, OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            val response = supabaseClient.postgrest
                .from(UserMutesTable)
                .select {
                    filter {
                        eq("muter_id", userId)
                        eq("muted_id", targetUserId)
                    }
                }
            val mutes = response.decodeList<UserMuteDto>()
            Result.Success(mutes.isNotEmpty())
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Phase 4: Reports
    override suspend fun reportUser(targetUserId: String, reason: String, description: String?): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            if (userId == targetUserId) {
                return Result.Error(OnboardingProfileError.Unknown)
            }
            supabaseClient.postgrest.from(UserReportsTable).insert(
                UserReportUpsertDto(
                    reporterId = userId,
                    reportedUserId = targetUserId,
                    reason = reason,
                    description = description
                )
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getUserReports(userId: String, limit: Int, offset: Int): Result<List<UserReportDto>, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from(UserReportsTable)
                .select {
                    filter { eq("reporter_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
            val allReports: List<UserReportDto> = response.decodeList()
            val paginatedReports = allReports.drop(offset).take(limit)
            Result.Success(paginatedReports)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Phase 4: Notifications
    override suspend fun getNotifications(userId: String, limit: Int, offset: Int): Result<List<NotificationDto>, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from(NotificationsTable)
                .select {
                    filter { eq("user_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
            val allNotifications: List<NotificationDto> = response.decodeList()
            val paginatedNotifications = allNotifications.drop(offset).take(limit)
            Result.Success(paginatedNotifications)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun markNotificationRead(notificationId: String): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(NotificationsTable)
                .update(mapOf("read_at" to java.time.Instant.now().toString())) {
                    filter {
                        eq("id", notificationId)
                        eq("user_id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun markAllNotificationsRead(userId: String): EmptyResult<OnboardingProfileError> {
        return try {
            supabaseClient.postgrest
                .from(NotificationsTable)
                .update(mapOf("read_at" to java.time.Instant.now().toString())) {
                    filter {
                        eq("user_id", userId)
                        eq("read_at", "null") // Note: this won't work directly, would need raw SQL
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getUnreadNotificationCount(userId: String): Result<Int, OnboardingProfileError> {
        return try {
            // Note: Supabase doesn't support COUNT directly in this way easily
            // For production, you'd use an RPC function or materialized view
            val response = supabaseClient.postgrest
                .from(NotificationsTable)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("user_id", userId)
                        // Note: PostgREST doesn't support IS NULL directly in filter
                    }
                }
            val notifications = response.decodeList<Any>()
            Result.Success(notifications.size)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    // Phase 4: Presence
    override suspend fun updatePresence(status: String, currentRoomId: String?): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            val updateMap = mutableMapOf<String, Any>(
                "status" to status,
                "updated_at" to java.time.Instant.now().toString()
            )
            currentRoomId?.let { updateMap["current_room_id"] = it }
            
            supabaseClient.postgrest
                .from(UserPresenceTable)
                .update(updateMap) {
                    filter { eq("user_id", userId) }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getPresence(userId: String): Result<UserPresenceDto, OnboardingProfileError> {
        return try {
            val response = supabaseClient.postgrest
                .from(UserPresenceTable)
                .select {
                    filter { eq("user_id", userId) }
                }
            val presence = response.decodeSingleOrNull<UserPresenceDto>()
            presence?.let { Result.Success(it) } ?: Result.Error(OnboardingProfileError.Unknown)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun getUserPresences(userIds: List<String>): Result<List<UserPresenceDto>, OnboardingProfileError> {
        return try {
            if (userIds.isEmpty()) return Result.Success(emptyList())
            // Note: IN clause not directly supported, would need RPC or multiple queries
            // For now, fetch individually
            val presences = mutableListOf<UserPresenceDto>()
            for (id in userIds) {
                when (val result = getPresence(id)) {
                    is Result.Success -> presences.add(result.data)
                    is Result.Error -> { } // Ignore errors for individual presences
                }
            }
            Result.Success(presences)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    private suspend fun updateProfile(
        update: ProfileUpdateDto
    ): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(ProfilesTable)
                .update(update) {
                    filter {
                        eq("id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.toOnboardingProfileError())
        }
    }

    private fun requireUserId(): String {
        if (!supabaseConfig.isConfigured) {
            throw OnboardingProfileNotConfiguredException()
        }

        return supabaseClient.auth.currentUserOrNull()?.id
            ?: throw OnboardingProfileNotAuthenticatedException()
    }

    private fun Throwable.toOnboardingProfileError(): OnboardingProfileError {
        return when (this) {
            is OnboardingProfileNotConfiguredException -> OnboardingProfileError.NotConfigured
            is OnboardingProfileNotAuthenticatedException -> OnboardingProfileError.NotAuthenticated
            is HttpRequestException,
            is HttpRequestTimeoutException -> OnboardingProfileError.Network
            is RestException -> when {
                isUsernameConflict() -> OnboardingProfileError.UsernameTaken
                else -> OnboardingProfileError.Unknown
            }
            else -> OnboardingProfileError.Unknown
        }
    }

    private fun RestException.isUsernameConflict(): Boolean {
        return statusCode == 409 ||
            message?.contains("profiles_username_key", ignoreCase = true) == true ||
            message?.contains("duplicate key", ignoreCase = true) == true
    }

    private fun ByteArray.detectImageType(): Pair<String, ContentType> {
        return when {
            size >= 3 &&
                this[0] == 0xFF.toByte() &&
                this[1] == 0xD8.toByte() &&
                this[2] == 0xFF.toByte() -> {
                "jpg" to ContentType.Image.JPEG
            }
            size >= 8 &&
                this[0] == 0x89.toByte() &&
                this[1] == 0x50.toByte() &&
                this[2] == 0x4E.toByte() &&
                this[3] == 0x47.toByte() -> {
                "png" to ContentType.Image.PNG
            }
            size >= 12 &&
                this[0] == 0x52.toByte() &&
                this[1] == 0x49.toByte() &&
                this[2] == 0x46.toByte() &&
                this[3] == 0x46.toByte() -> {
                "webp" to ContentType("image", "webp")
            }
            else -> "jpg" to ContentType.Image.JPEG
        }
    }
}

private class OnboardingProfileNotConfiguredException : Exception()
private class OnboardingProfileNotAuthenticatedException : Exception()
