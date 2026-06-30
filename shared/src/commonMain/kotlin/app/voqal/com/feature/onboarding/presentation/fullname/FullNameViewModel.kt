package app.voqal.com.feature.onboarding.presentation.fullname


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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FullNameViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingProfileDataSource: OnboardingProfileDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(
        FullNameState(
            firstName = savedStateHandle["firstName"] ?: onboardingDraftStore.firstName,
            lastName = savedStateHandle["lastName"] ?: onboardingDraftStore.lastName
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<FullNameEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            onboardingDraftStore.draft.collectLatest { draft ->
                _state.update {
                    it.copy(
                        firstName = it.firstName.ifBlank { draft.firstName },
                        lastName = it.lastName.ifBlank { draft.lastName }
                    )
                }
            }
        }
    }

    fun onAction(action: FullNameAction) {
        when (action) {
            is FullNameAction.OnFirstNameChange -> {
                onboardingDraftStore.updateFirstName(action.value)
                savedStateHandle["firstName"] = action.value
                _state.update { it.copy(firstName = action.value, error = null) }
            }
            is FullNameAction.OnLastNameChange -> {
                onboardingDraftStore.updateLastName(action.value)
                savedStateHandle["lastName"] = action.value
                _state.update { it.copy(lastName = action.value, error = null) }
            }
            FullNameAction.OnContinueClick -> submitFullName()
        }
    }

    private fun submitFullName() {
        val currentState = state.value
        if (!currentState.isFormValid || currentState.isLoading) return

        viewModelScope.launch {
            _events.send(FullNameEvent.Navigate)
        }
    }
}
