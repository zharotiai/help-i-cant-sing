package io.github.zharotiai.help_i_cant_sing

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform