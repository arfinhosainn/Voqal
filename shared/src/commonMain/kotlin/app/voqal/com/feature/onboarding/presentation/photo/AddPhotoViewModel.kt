package app.voqal.com.feature.onboarding.presentation.photo


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPhotoViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddPhotoState())
    val state = _state.asStateFlow()

    private val _events = Channel<AddPhotoEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: AddPhotoAction) {
        when (action) {
            is AddPhotoAction.OnPhotoSelected -> {
                _state.update { it.copy(profilePhotoUri = action.bytes, error = null) }
            }
            AddPhotoAction.OnContinueClick -> {
                saveProfilePhotoAndProceed()
            }
        }
    }

    private fun saveProfilePhotoAndProceed() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                // Perform any validation or local persistence checks here
                _events.send(AddPhotoEvent.NavigateToNext)
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false) }
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}