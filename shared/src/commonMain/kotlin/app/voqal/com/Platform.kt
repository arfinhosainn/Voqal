package app.voqal.com

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform