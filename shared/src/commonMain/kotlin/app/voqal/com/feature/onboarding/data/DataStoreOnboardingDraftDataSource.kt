package app.voqal.com.feature.onboarding.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.voqal.com.feature.onboarding.domain.OnboardingDraft
import app.voqal.com.feature.onboarding.domain.OnboardingDraftLocalDataSource
import kotlinx.coroutines.flow.first

class DataStoreOnboardingDraftDataSource(
    private val dataStore: DataStore<Preferences>
) : OnboardingDraftLocalDataSource {

    override suspend fun getDraft(): OnboardingDraft {
        val preferences = dataStore.data.first()

        return OnboardingDraft(
            email = preferences[EmailKey].orEmpty(),
            firstName = preferences[FirstNameKey].orEmpty(),
            lastName = preferences[LastNameKey].orEmpty(),
            username = preferences[UsernameKey].orEmpty(),
            profilePhotoUri = preferences[ProfilePhotoUriKey],
            selectedLanguageId = preferences[SelectedLanguageIdKey],
            selectedInterestIds = preferences[SelectedInterestIdsKey]
                ?.split(InterestSeparator)
                ?.filter { it.isNotBlank() }
                ?.toSet()
                ?: emptySet(),
            currentStep = preferences[CurrentStepKey] ?: 1,
            lastUpdatedAtMillis = preferences[LastUpdatedAtMillisKey] ?: 0L
        )
    }

    override suspend fun saveDraft(draft: OnboardingDraft) {
        dataStore.edit { preferences ->
            preferences[EmailKey] = draft.email
            preferences[FirstNameKey] = draft.firstName
            preferences[LastNameKey] = draft.lastName
            preferences[UsernameKey] = draft.username
            preferences[CurrentStepKey] = draft.currentStep
            preferences[LastUpdatedAtMillisKey] = draft.lastUpdatedAtMillis

            draft.profilePhotoUri?.let {
                preferences[ProfilePhotoUriKey] = it
            } ?: preferences.remove(ProfilePhotoUriKey)

            draft.selectedLanguageId?.let {
                preferences[SelectedLanguageIdKey] = it
            } ?: preferences.remove(SelectedLanguageIdKey)

            if (draft.selectedInterestIds.isEmpty()) {
                preferences.remove(SelectedInterestIdsKey)
            } else {
                preferences[SelectedInterestIdsKey] = draft.selectedInterestIds.joinToString(
                    separator = InterestSeparator
                )
            }
        }
    }

    override suspend fun clearDraft() {
        dataStore.edit { it.clear() }
    }

    private companion object {
        const val InterestSeparator = ","

        val EmailKey = stringPreferencesKey("onboarding_email")
        val FirstNameKey = stringPreferencesKey("onboarding_first_name")
        val LastNameKey = stringPreferencesKey("onboarding_last_name")
        val UsernameKey = stringPreferencesKey("onboarding_username")
        val ProfilePhotoUriKey = stringPreferencesKey("onboarding_profile_photo_uri")
        val SelectedLanguageIdKey = stringPreferencesKey("onboarding_selected_language_id")
        val SelectedInterestIdsKey = stringPreferencesKey("onboarding_selected_interest_ids")
        val CurrentStepKey = intPreferencesKey("onboarding_current_step")
        val LastUpdatedAtMillisKey = longPreferencesKey("onboarding_last_updated_at_millis")
    }
}
