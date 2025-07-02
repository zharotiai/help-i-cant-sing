package io.github.zharotiai.help_i_cant_sing.audio.detect_pitch

interface PitchDetector {
    /**
     * Detects the pitch (in Hz) from a PCM audio buffer.
     * @param buffer The audio buffer (PCM 16-bit samples)
     * @param sampleRate The sample rate of the audio buffer
     * @return The detected pitch in Hz, or null if no pitch is detected
     */
    fun detectPitch(buffer: ShortArray, sampleRate: Int): Float?

    fun getName(): String
}