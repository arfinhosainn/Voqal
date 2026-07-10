package app.voqal.com.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun hasSeenRaiseHandEducation(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[HasSeenRaiseHandEducationKey] ?: false
        }.first()
    }

    suspend fun setHasSeenRaiseHandEducation(seen: Boolean) {
        dataStore.edit { preferences ->
            preferences[HasSeenRaiseHandEducationKey] = seen
        }
    }

    suspend fun getStoredRoom(): StoredRoomData? {
        return dataStore.data.map { preferences ->
            preferences[StoredRoomKey]?.let { json ->
                try {
                    Json.decodeFromString<StoredRoomData>(json)
                } catch (e: Exception) {
                    null
                }
            }
        }.first()
    }

    suspend fun setStoredRoom(data: StoredRoomData?) {
        dataStore.edit { preferences ->
            if (data != null) {
                preferences[StoredRoomKey] = Json.encodeToString(StoredRoomData.serializer(), data)
            } else {
                preferences.remove(StoredRoomKey)
            }
        }
    }

    private companion object {
        val HasSeenRaiseHandEducationKey = booleanPreferencesKey("has_seen_raise_hand_education")
        val StoredRoomKey = stringPreferencesKey("stored_room_data")
    }
}
