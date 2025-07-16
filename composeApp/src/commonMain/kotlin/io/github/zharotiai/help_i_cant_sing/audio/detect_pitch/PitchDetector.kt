package io.github.zharotiai.help_i_cant_sing.audio.detect_pitch

interface PitchDetector {
    fun detectPitch(buffer: ShortArray, sampleRate: Int): Float?

    fun getName(): String
}