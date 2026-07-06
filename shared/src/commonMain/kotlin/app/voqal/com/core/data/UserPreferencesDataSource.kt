package app.voqal.com.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

    private companion object {
        val HasSeenRaiseHandEducationKey = booleanPreferencesKey("has_seen_raise_hand_education")
    }
}
