package io.github.zharotiai.help_i_cant_sing.ui

import kotlin.math.ln
import kotlin.math.roundToInt

fun freqToMidi(freq: Float): Int {
    return (69 + 12 * log2(freq / 440f)).roundToInt()
}

fun log2(value: Float): Float = ln(value) / ln(2f)
