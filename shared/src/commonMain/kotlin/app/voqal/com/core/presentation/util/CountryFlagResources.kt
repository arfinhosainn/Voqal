package app.voqal.com.core.presentation.util

import org.jetbrains.compose.resources.DrawableResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.chinese
import voqal.shared.generated.resources.czech
import voqal.shared.generated.resources.danish
import voqal.shared.generated.resources.english
import voqal.shared.generated.resources.finnish
import voqal.shared.generated.resources.french
import voqal.shared.generated.resources.german
import voqal.shared.generated.resources.hungarian
import voqal.shared.generated.resources.indonesian
import voqal.shared.generated.resources.irish
import voqal.shared.generated.resources.italian
import voqal.shared.generated.resources.korean
import voqal.shared.generated.resources.persian
import voqal.shared.generated.resources.polish
import voqal.shared.generated.resources.portuguese
import voqal.shared.generated.resources.russian
import voqal.shared.generated.resources.spanish
import voqal.shared.generated.resources.swedish
import voqal.shared.generated.resources.turkish
import voqal.shared.generated.resources.ukrainian

object CountryFlagResources {
    private val flags = mapOf(
        "CZ" to Res.drawable.czech,
        "IE" to Res.drawable.irish,
        "DK" to Res.drawable.danish,
        "FR" to Res.drawable.french,
        "DE" to Res.drawable.german,
        "KR" to Res.drawable.korean,
        "PL" to Res.drawable.polish,
        "CN" to Res.drawable.chinese,
        "GB" to Res.drawable.english,
        "FI" to Res.drawable.finnish,
        "IT" to Res.drawable.italian,
        "IR" to Res.drawable.persian,
        "RU" to Res.drawable.russian,
        "ES" to Res.drawable.spanish,
        "SE" to Res.drawable.swedish,
        "TR" to Res.drawable.turkish,
        "HU" to Res.drawable.hungarian,
        "UA" to Res.drawable.ukrainian,
        "ID" to Res.drawable.indonesian,
        "PT" to Res.drawable.portuguese,
    )

    fun resolve(countryCode: String?): DrawableResource? {
        val normalized = countryCode?.trim()?.uppercase()
        return flags[normalized]
    }
}
