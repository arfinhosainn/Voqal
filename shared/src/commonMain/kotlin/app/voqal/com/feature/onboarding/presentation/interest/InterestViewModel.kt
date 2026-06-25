package app.voqal.com.feature.onboarding.presentation.interest


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
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

class ChooseInterestsViewModel(
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingProfileDataSource: OnboardingProfileDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChooseInterestsState(
            selectedInterestIds = onboardingDraftStore.selectedInterestIds
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<ChooseInterestsEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            onboardingDraftStore.draft.collectLatest { draft ->
                if (_state.value.selectedInterestIds.isEmpty() && draft.selectedInterestIds.isNotEmpty()) {
                    _state.update { it.copy(selectedInterestIds = draft.selectedInterestIds) }
                }
            }
        }
    }

    fun onAction(action: ChooseInterestsAction) {
        when (action) {
            is ChooseInterestsAction.OnInterestToggle -> handleInterestToggle(action.interestId)
            ChooseInterestsAction.OnContinueClick -> submitSelectedInterests()
        }
    }

    private fun handleInterestToggle(interestId: String) {
        var updatedSelection = emptySet<String>()

        _state.update { currentState ->
            updatedSelection = if (currentState.selectedInterestIds.contains(interestId)) {
                currentState.selectedInterestIds - interestId
            } else {
                currentState.selectedInterestIds + interestId
            }
            currentState.copy(selectedInterestIds = updatedSelection)
        }

        onboardingDraftStore.updateSelectedInterestIds(updatedSelection)
    }

    private fun submitSelectedInterests() {
        val selectedIds = state.value.selectedInterestIds
        if (!state.value.canContinue || state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }

            when (val result = onboardingProfileDataSource.completeOnboarding(selectedIds)) {
                is Result.Success -> {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(ChooseInterestsEvent.NavigateToNext(selectedIds.toList()))
                }
                is Result.Failure -> {
                    val message = result.error.toUserMessage()
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(ChooseInterestsEvent.ShowSnackbar(message))
                }
            }
        }
    }
}
