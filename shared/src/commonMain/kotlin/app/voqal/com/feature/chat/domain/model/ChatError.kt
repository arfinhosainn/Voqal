package app.voqal.com.feature.chat.domain.model

import app.voqal.com.core.domain.Error

enum class ChatError : Error {
    NETWORK,
    UNKNOWN,
    MESSAGE_TOO_LONG,
    NOT_AUTHENTICATED
}
