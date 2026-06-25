package app.voqal.com.feature.onboarding.presentation.otp

sealed interface OtpEvent {
    data object NavigateToNext : OtpEvent
    data object NavigateToEmail : OtpEvent
    data class FocusField(val index: Int) : OtpEvent
    data class ShowSnackbar(val message: String) : OtpEvent
}
