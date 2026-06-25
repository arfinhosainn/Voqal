package app.voqal.com.feature.onboarding.presentation.photo

import app.voqal.com.core.presentation.util.UiText

data class AddPhotoState(
    val profilePhotoUri: ByteArray? = null,
    val isSubmitting: Boolean = false,
    val error: UiText? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AddPhotoState

        if (isSubmitting != other.isSubmitting) return false
        if (!profilePhotoUri.contentEquals(other.profilePhotoUri)) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isSubmitting.hashCode()
        result = 31 * result + (profilePhotoUri?.contentHashCode() ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}