package app.voqal.com.feature.room.domain

import app.voqal.com.core.data.UserPreferencesDataSource
import app.voqal.com.core.domain.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class RoomRecoveryManager(
    private val supabaseClient: SupabaseClient,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val roomDiscoveryRepository: RoomDiscoveryRepository
) {
    suspend fun recover() {
        val stored = userPreferencesDataSource.getStoredRoom() ?: return

        try {
            supabaseClient.auth.awaitInitialization()
        } catch (e: Exception) {
            return
        }

        if (supabaseClient.auth.currentUserOrNull() == null) {
            return
        }

        val leaveResult = roomDiscoveryRepository.leaveRoom(stored.roomId)
        if (leaveResult is Result.Error) {
            return
        }

        if (stored.wasHost) {
            roomDiscoveryRepository.deleteRoom(stored.roomId)
        }

        userPreferencesDataSource.setStoredRoom(null)
    }
}
