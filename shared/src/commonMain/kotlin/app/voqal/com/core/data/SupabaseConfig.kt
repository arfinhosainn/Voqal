package app.voqal.com.core.data

data class SupabaseConfig(
    val url: String,
    val publishableKey: String
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() &&
            publishableKey.isNotBlank() &&
            "YOUR_PROJECT_REF" !in url &&
            "YOUR_SUPABASE_PUBLISHABLE_KEY" !in publishableKey
}

object VoqalSupabaseConfig {
    val current = SupabaseConfig(
        url = "https://bykulndzmnkfkgypgaae.supabase.co",
        publishableKey = "sb_publishable_GcXeC-MUDmjp003HQJZhcw_6flp0q4a"
    )
}

