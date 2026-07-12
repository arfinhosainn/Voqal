package app.voqal.com.core.util

object NumberFormatter {
    fun toShortCount(value: Int): String = when {
        value >= 1_000_000 -> "${"%.1f".format(value / 1_000_000f)}M"
        value >= 1_000 -> "${"%.1f".format(value / 1_000f)}k"
        else -> value.toString()
    }
}
