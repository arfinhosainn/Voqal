package app.voqal.com.feature.chat.domain.model

sealed interface ChatContent {
    data class Text(val body: String) : ChatContent
    data class Image(val url: String, val caption: String? = null) : ChatContent
    // Future types: Voice, Gif, File
}
