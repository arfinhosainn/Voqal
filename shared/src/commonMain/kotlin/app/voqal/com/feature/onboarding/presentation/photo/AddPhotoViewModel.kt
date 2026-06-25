package app.voqal.com.feature.onboarding.presentation.photo


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.onboarding.domain.OnboardingProfileDataSource
import app.voqal.com.feature.onboarding.domain.toUserMessage
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PROFILE_PHOTO_BYTES_KEY = "profilePhotoBytes"

class AddPhotoViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingProfileDataSource: OnboardingProfileDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(
        AddPhotoState(
            profilePhotoUri = savedStateHandle[PROFILE_PHOTO_BYTES_KEY]
                ?: onboardingDraftStore.profilePhotoBytes
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<AddPhotoEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: AddPhotoAction) {
        when (action) {
            is AddPhotoAction.OnPhotoSelected -> {
                onboardingDraftStore.updateProfilePhotoBytes(action.bytes)
                savedStateHandle[PROFILE_PHOTO_BYTES_KEY] = action.bytes
                _state.update { it.copy(profilePhotoUri = action.bytes, error = null) }
            }
            AddPhotoAction.OnContinueClick -> {
                saveProfilePhotoAndProceed()
            }
        }
    }

    private fun saveProfilePhotoAndProceed() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }

            val photoBytes = state.value.profilePhotoUri ?: ByteArray(0)
            when (val result = onboardingProfileDataSource.uploadAvatar(photoBytes)) {
                is Result.Success -> {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(AddPhotoEvent.NavigateToNext)
                }
                is Result.Failure -> {
                    val message = UiText.DynamicString(result.error.toUserMessage())
                    _state.update { it.copy(isSubmitting = false, error = message) }
                    _events.send(AddPhotoEvent.ShowSnackbar(message))
                }
            }
        }
    }
}
