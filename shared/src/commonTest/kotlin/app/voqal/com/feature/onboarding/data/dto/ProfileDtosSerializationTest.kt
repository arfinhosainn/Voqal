package app.voqal.com.feature.onboarding.data.dto

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProfileDtosSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `ProfileUpdateDto serializes with new fields`() {
        val dto = ProfileUpdateDto(
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            bio = "Software developer",
            occupation = "Engineer",
            website = "https://johndoe.com",
            birthday = "1990-01-15",
            allowFollowing = true,
            allowDm = false,
            showOnline = true
        )

        val jsonString = json.encodeToString(dto)
        val decoded = json.decodeFromString<ProfileUpdateDto>(jsonString)

        assertEquals("John", decoded.firstName)
        assertEquals("Doe", decoded.lastName)
        assertEquals("johndoe", decoded.username)
        assertEquals("Software developer", decoded.bio)
        assertEquals("Engineer", decoded.occupation)
        assertEquals("https://johndoe.com", decoded.website)
        assertEquals("1990-01-15", decoded.birthday)
        assertEquals(true, decoded.allowFollowing)
        assertEquals(false, decoded.allowDm)
        assertEquals(true, decoded.showOnline)
    }

    @Test
    fun `SocialLinkUpsertDto serializes correctly`() {
        val dto = SocialLinkUpsertDto(
            userId = "user-123",
            platform = "instagram",
            url = "https://instagram.com/johndoe",
            username = "johndoe",
            visibility = "public",
            verified = false
        )

        val jsonString = json.encodeToString(dto)
        val decoded = json.decodeFromString<SocialLinkUpsertDto>(jsonString)

        assertEquals("user-123", decoded.userId)
        assertEquals("instagram", decoded.platform)
        assertEquals("https://instagram.com/johndoe", decoded.url)
        assertEquals("johndoe", decoded.username)
        assertEquals("public", decoded.visibility)
        assertEquals(false, decoded.verified)
    }

    @Test
    fun `SocialLinkDto deserializes from database`() {
        val jsonString = """
            {
                "id": "link-123",
                "user_id": "user-123",
                "platform": "twitter",
                "url": "https://twitter.com/johndoe",
                "username": "johndoe",
                "visibility": "followers",
                "verified": true,
                "created_at": "2024-01-15T10:30:00Z"
            }
        """.trimIndent()

        val decoded = json.decodeFromString<SocialLinkDto>(jsonString)

        assertEquals("link-123", decoded.id)
        assertEquals("user-123", decoded.userId)
        assertEquals("twitter", decoded.platform)
        assertEquals("https://twitter.com/johndoe", decoded.url)
        assertEquals("johndoe", decoded.username)
        assertEquals("followers", decoded.visibility)
        assertEquals(true, decoded.verified)
        assertEquals("2024-01-15T10:30:00Z", decoded.createdAt)
    }

    @Test
    fun `InterestDto serializes correctly`() {
        val dto = InterestDto(
            id = "coding_dev",
            name = "Coding & Dev",
            emoji = "💻",
            category = "tech"
        )

        val jsonString = json.encodeToString(dto)
        val decoded = json.decodeFromString<InterestDto>(jsonString)

        assertEquals("coding_dev", decoded.id)
        assertEquals("Coding & Dev", decoded.name)
        assertEquals("💻", decoded.emoji)
        assertEquals("tech", decoded.category)
    }

    @Test
    fun `InterestCategoryDto serializes with display_order`() {
        val jsonString = """
            {
                "id": "tech",
                "name": "Tech & Innovation",
                "display_order": 1
            }
        """.trimIndent()

        val decoded = json.decodeFromString<InterestCategoryDto>(jsonString)

        assertEquals("tech", decoded.id)
        assertEquals("Tech & Innovation", decoded.name)
        assertEquals(1, decoded.displayOrder)
    }

    @Test
    fun `CompleteOnboardingParams serializes interest list`() {
        val params = CompleteOnboardingParams(
            interestIds = listOf("coding_dev", "ai_futurism", "gaming_esports")
        )

        val jsonString = json.encodeToString(params)
        val decoded = json.decodeFromString<CompleteOnboardingParams>(jsonString)

        assertEquals(3, decoded.interestIds.size)
        assertTrue(decoded.interestIds.contains("coding_dev"))
        assertTrue(decoded.interestIds.contains("ai_futurism"))
        assertTrue(decoded.interestIds.contains("gaming_esports"))
    }
}