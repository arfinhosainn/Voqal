package app.voqal.com.core.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientFactory {
    fun create(config: SupabaseConfig): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = config.url,
            supabaseKey = config.publishableKey
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}

