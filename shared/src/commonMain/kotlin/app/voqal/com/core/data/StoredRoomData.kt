package app.voqal.com.core.data

import kotlinx.serialization.Serializable

@Serializable
data class StoredRoomData(
    val roomId: String,
    val wasHost: Boolean,
    val createdAt: Long,
    val roomType: String? = null,
    val cleanupVersion: Int = 1
)
