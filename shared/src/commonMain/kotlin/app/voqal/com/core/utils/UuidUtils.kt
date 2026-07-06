package app.voqal.com.core.utils

import kotlin.random.Random

object UuidUtils {

    fun randomUuid(): String {
        val chars = "0123456789abcdef"
        return buildString {
            repeat(8) { append(chars[Random.nextInt(16)]) }
            append("-")
            repeat(4) { append(chars[Random.nextInt(16)]) }
            append("-4") // Version 4
            repeat(3) { append(chars[Random.nextInt(16)]) }
            append("-")
            append(chars[8 + Random.nextInt(4)]) // Variant 10xx
            repeat(3) { append(chars[Random.nextInt(16)]) }
            append("-")
            repeat(12) { append(chars[Random.nextInt(16)]) }
        }
    }
}
