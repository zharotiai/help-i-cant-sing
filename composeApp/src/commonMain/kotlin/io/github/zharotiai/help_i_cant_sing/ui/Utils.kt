package io.github.zharotiai.help_i_cant_sing.ui

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

fun freqToMidi(freq: Float): Int {
    return (69 + 12 * log2(freq / 440f)).roundToInt()
}

fun midiToFreq(midi: Int): Double = 440.0 * 2.0.pow((midi - 69) / 12.0)

fun log2(value: Float): Float = ln(value) / ln(2f)

fun freqToY(freq: Float, logMin: Float, height: Float, pixelsPerOctave: Float): Float {
    val logFreq = log2(freq.coerceAtLeast(0.1f))
    val octavesFromMin = logFreq - logMin
    return height - (octavesFromMin * pixelsPerOctave)
}