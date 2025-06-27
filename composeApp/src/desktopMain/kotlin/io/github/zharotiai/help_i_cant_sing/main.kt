package io.github.zharotiai.help_i_cant_sing

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "help_i_cant_sing",
    ) {
        App()
    }
}