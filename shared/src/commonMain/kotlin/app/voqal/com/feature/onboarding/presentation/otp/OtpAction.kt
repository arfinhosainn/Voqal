package app.voqal.com.feature.onboarding.presentation.otp

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnChangeFieldFocused(val index: Int) : OtpAction
    data object OnKeyboardBack : OtpAction
    data object OnResendCodeClick : OtpAction
    data object OnVerifyClick : OtpAction
}