package app.voqal.com.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private const val OnboardingDraftDataStoreFileName = "onboarding_draft.preferences_pb"
private const val UserPreferencesDataStoreFileName = "user_preferences.preferences_pb"

fun createOnboardingDraftDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            getDataStorePath(OnboardingDraftDataStoreFileName).toPath()
        }
    )
}

fun createUserPreferencesDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            getDataStorePath(UserPreferencesDataStoreFileName).toPath()
        }
    )
}

expect fun getDataStorePath(fileName: String): String
