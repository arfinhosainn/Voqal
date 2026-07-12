package app.voqal.com.core.util

object NumberFormatter {
    fun toShortCount(value: Int): String = when {
        value >= 1_000_000 -> {
            val millions = value / 1_000_000.0
            val rounded = (millions * 10).toInt() / 10.0
            "$rounded M"
        }
        value >= 1_000 -> {
            val thousands = value / 1_000.0
            val rounded = (thousands * 10).toInt() / 10.0
            "$rounded k"
        }
        else -> value.toString()
    }
}
