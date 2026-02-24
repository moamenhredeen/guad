package app.guad

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform