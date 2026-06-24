package app.voqal.com.feature.onboarding.presentation.language

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
import voqal.shared.generated.resources.russian
import voqal.shared.generated.resources.spanish
import voqal.shared.generated.resources.swedish
import voqal.shared.generated.resources.turkish
import voqal.shared.generated.resources.ukrainian

data class LanguageState(
    val languages: List<LanguageUi> = listOf(
        LanguageUi("de", "German", Res.drawable.german),
        LanguageUi("zh", "Chinese", Res.drawable.chinese),
        LanguageUi("ko", "Korean", Res.drawable.korean),
        LanguageUi("en", "English", Res.drawable.english),
        LanguageUi("fr", "French", Res.drawable.french),
        LanguageUi("id", "Indonesian", Res.drawable.indonesian),
        LanguageUi("fa", "Persian", Res.drawable.persian),
        LanguageUi("hu", "Hungarian", Res.drawable.hungarian),
        LanguageUi("it", "Italian", Res.drawable.italian),
        LanguageUi("sv", "Swedish", Res.drawable.swedish),
        LanguageUi("cs", "Czech", Res.drawable.czech),
        LanguageUi("da", "Danish", Res.drawable.danish),
        LanguageUi("fi", "Finnish", Res.drawable.finnish),
        LanguageUi("ga", "Irish", Res.drawable.irish),
        LanguageUi("pl", "Polish", Res.drawable.polish),
        LanguageUi("ru", "Russian", Res.drawable.russian),
        LanguageUi("es", "Spanish", Res.drawable.spanish),
        LanguageUi("tr", "Turkish", Res.drawable.turkish),
        LanguageUi("uk", "Ukrainian", Res.drawable.ukrainian),
    ),
    val selectedLanguage: LanguageUi? = null,
    val isSubmitting: Boolean = false
)