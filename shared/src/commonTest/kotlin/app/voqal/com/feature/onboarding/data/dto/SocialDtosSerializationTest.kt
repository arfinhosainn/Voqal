package app.voqal.com.feature.onboarding.data.dto

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SocialDtosSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `FollowDto serializes and deserializes correctly`() {
        val dto = FollowDto(
            followerId = "user-123",
            followingId = "user-456",
            createdAt = "2024-01-15T10:30:00Z"
        )

        val jsonString = json.encodeToString(dto)
        val decoded = json.decodeFromString<FollowDto>(jsonString)

        assertEquals("user-123", decoded.followerId)
        assertEquals("user-456", decoded.followingId)
        assertEquals("2024-01-15T10:30:00Z", decoded.createdAt)
    }

    @Test
    fun `FollowUpsertDto serializes with snake_case`() {
        val dto = FollowUpsertDto(
            followerId = "user-123",
            followingId = "user-456"
        )

        val jsonString = json.encodeToString(dto)
        // Should use snake_case for column names
        assertTrue(jsonString.contains("follower_id"))
        assertTrue(jsonString.contains("following_id"))
    }

    @Test
    fun `ProfileStatsDto deserializes from database`() {
        val jsonString = """
            {
                "user_id": "user-123",
                "followers_count": 100,
                "following_count": 50,
                "rooms_joined": 25,
                "rooms_hosted": 10,
                "rooms_created": 5,
                "likes_received": 200,
                "last_updated": "2024-01-15T12:00:00Z"
            }
        """.trimIndent()

        val decoded = json.decodeFromString<ProfileStatsDto>(jsonString)

        assertEquals("user-123", decoded.userId)
        assertEquals(100, decoded.followersCount)
        assertEquals(50, decoded.followingCount)
        assertEquals(25, decoded.roomsJoined)
        assertEquals(10, decoded.roomsHosted)
        assertEquals(5, decoded.roomsCreated)
        assertEquals(200, decoded.likesReceived)
        assertEquals("2024-01-15T12:00:00Z", decoded.lastUpdated)
    }
}