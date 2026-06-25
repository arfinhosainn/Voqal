package app.voqal.com.feature.onboarding.presentation.photo

sealed interface AddPhotoAction {
    data class OnPhotoSelected(val bytes: ByteArray) : AddPhotoAction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as OnPhotoSelected

            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data object OnContinueClick : AddPhotoAction
}