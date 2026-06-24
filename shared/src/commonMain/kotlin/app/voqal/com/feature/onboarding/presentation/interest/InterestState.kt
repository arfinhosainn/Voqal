package app.voqal.com.feature.onboarding.presentation.interest

data class ChooseInterestsState(
    val availableInterests: List<InterestItem> = listOf(
        // 🚀 Tech & Innovation
        InterestItem("1", "AI & Futurism", "🤖", "Tech & Innovation"),
        InterestItem("2", "Startups & VC", "🚀", "Tech & Innovation"),
        InterestItem("3", "Coding & Dev", "💻", "Tech & Innovation"),
        InterestItem("4", "Crypto & Web3", "🪙", "Tech & Innovation"),
        InterestItem("5", "Gadgets & Gear", "🔌", "Tech & Innovation"),

        // 📈 Business & Money
        InterestItem("6", "Investing & Stocks", "📈", "Business & Money"),
        InterestItem("7", "Marketing & Growth", "📣", "Business & Money"),
        InterestItem("8", "Side Hustles", "💼", "Business & Money"),
        InterestItem("9", "Real Estate", "🏢", "Business & Money"),
        InterestItem("10", "Creator Economy", "📸", "Business & Money"),

        // 🎵 Entertainment & Arts
        InterestItem("11", "Music & Beats", "🎵", "Entertainment & Arts"),
        InterestItem("12", "Movies & Shows", "🎬", "Entertainment & Arts"),
        InterestItem("13", "Gaming & Esports", "🎮", "Entertainment & Arts"),
        InterestItem("14", "Anime & Manga", "🎏", "Entertainment & Arts"),
        InterestItem("15", "Standup Comedy", "🎤", "Entertainment & Arts"),

        // 🧠 Health & Mindset
        InterestItem("16", "Mental Health", "🧠", "Health & Mindset"),
        InterestItem("17", "Fitness & Gym", "💪", "Health & Mindset"),
        InterestItem("18", "Meditation & Zen", "🧘", "Health & Mindset"),
        InterestItem("19", "Biohacking & Diet", "🧪", "Health & Mindset"),

        // 🌍 Lifestyle & Culture
        InterestItem("20", "Travel & Adventure", "✈️", "Lifestyle & Culture"),
        InterestItem("21", "Books & Literature", "📚", "Lifestyle & Culture"),
        InterestItem("22", "Food & Cooking", "🍳", "Lifestyle & Culture"),
        InterestItem("23", "Dating & Relationships", "❤️", "Lifestyle & Culture"),
        InterestItem("24", "Fashion & Style", "✨", "Lifestyle & Culture"),

        // 🏛️ Deep Conversations
        InterestItem("25", "Philosophy", "🏛️", "Deep Conversations"),
        InterestItem("26", "True Crime & Mystery", "🕵️", "Deep Conversations"),
        InterestItem("27", "History & Myths", "📜", "Deep Conversations"),
        InterestItem("28", "Current Events", "🌍", "Deep Conversations"),
        InterestItem("29", "Pop Culture", "🍿", "Deep Conversations")
    ),
    val selectedInterestIds: Set<String> = emptySet(),
    val isSubmitting: Boolean = false
) {
    // Memoized optimization grouping evaluation layout
    val categorizedInterests: Map<String, List<InterestItem>> =
        availableInterests.groupBy { it.category }
}