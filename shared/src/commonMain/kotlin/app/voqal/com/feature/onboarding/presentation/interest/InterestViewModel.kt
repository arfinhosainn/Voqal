package app.voqal.com.feature.onboarding.presentation.interest


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChooseInterestsViewModel(
    private val onboardingDraftStore: OnboardingDraftStore
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
        val selectedIds = state.value.selectedInterestIds.toList()
        if (!state.value.canContinue) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                // Future Hook: Save profile preferences to remote cluster or database here
                _events.send(ChooseInterestsEvent.NavigateToNext(selectedIds))
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false) }
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
