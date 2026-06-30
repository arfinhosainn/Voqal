package app.voqal.com.feature.onboarding.presentation

import app.voqal.com.feature.onboarding.domain.OnboardingDraft
import app.voqal.com.feature.onboarding.domain.OnboardingDraftLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingDraftStore(
    private val localDataSource: OnboardingDraftLocalDataSource
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _draft = MutableStateFlow(OnboardingDraft())
    val draft = _draft.asStateFlow()

    val email: String get() = _draft.value.email
    val firstName: String get() = _draft.value.firstName
    val lastName: String get() = _draft.value.lastName
    val username: String get() = _draft.value.username
    var profilePhotoBytes: ByteArray? = null
        private set
    val selectedLanguageId: String? get() = _draft.value.selectedLanguageId
    val selectedInterestIds: Set<String> get() = _draft.value.selectedInterestIds

    init {
        scope.launch {
            _draft.value = localDataSource.getDraft()
        }
    }

    fun updateEmail(email: String) {
        updateDraft(currentStep = 1) { it.copy(email = email) }
    }

    fun updateFirstName(firstName: String) {
        updateDraft(currentStep = 3) { it.copy(firstName = firstName) }
    }

    fun updateLastName(lastName: String) {
        updateDraft(currentStep = 3) { it.copy(lastName = lastName) }
    }

    fun updateUsername(username: String) {
        updateDraft(currentStep = 4) { it.copy(username = username) }
    }

    fun updateSelectedLanguageId(languageId: String?) {
        updateDraft(currentStep = 6) { it.copy(selectedLanguageId = languageId) }
    }

    fun updateSelectedInterestIds(interestIds: Set<String>) {
        updateDraft(currentStep = 7) { it.copy(selectedInterestIds = interestIds) }
    }

    fun updateProfilePhotoUri(uri: String?) {
        updateDraft(currentStep = 5) { it.copy(profilePhotoUri = uri) }
    }

    fun updateProfilePhotoBytes(bytes: ByteArray?) {
        profilePhotoBytes = bytes
        updateDraft(currentStep = 5) { it }
    }

    fun clear() {
        profilePhotoBytes = null
        _draft.value = OnboardingDraft()
        scope.launch {
            localDataSource.clearDraft()
        }
    }

    private fun updateDraft(
        currentStep: Int,
        transform: (OnboardingDraft) -> OnboardingDraft
    ) {
        _draft.update {
            transform(it).copy(
                currentStep = currentStep
            )
        }

        val updatedDraft = _draft.value
        scope.launch {
            localDataSource.saveDraft(updatedDraft)
        }
    }
}
