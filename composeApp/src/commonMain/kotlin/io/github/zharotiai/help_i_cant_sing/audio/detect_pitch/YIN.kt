package io.github.zharotiai.help_i_cant_sing.audio.detect_pitch

import kotlin.math.abs
import kotlin.math.min

class YIN : PitchDetector {
    private val _name = "YIN"

    private val threshold: Float = 0.3f
    private val minFrequency = 80 // Roughly the human vocal range
    private val maxFrequency = 1000


    override fun getName(): String = _name

    override fun detectPitch(buffer: ShortArray, sampleRate: Int): Float? {
        val bufferSize = buffer.size
        val maxTau = sampleRate / minFrequency // lag bounds
        val minTau = sampleRate / maxFrequency

        // the buffer size needs to be large enough so that we can safely perform autocorrelation
        // we need at least two more samples greater than maxTau
        if (bufferSize < maxTau + 2) return null


        // placeholder array
        // will store the difference function at each time lag
        // from 1 to maxTau - 1
        val yinBuffer = FloatArray(maxTau)

        // 1. Calculate the difference function for each time lag
        for (tau in 1..<maxTau) {
            var sum: Float = 0f
            for (i in 0..<bufferSize - tau) {
                val diff = buffer[i].toFloat() - buffer[i+tau].toFloat()
                sum += diff * diff
            }
            yinBuffer[tau] = sum
        }

        // 2. perform a cumulative mean normalisation
        // stabilises the curve and makes it easier to work with
        yinBuffer[0] = 1f // undefined at 0, set to 1 for safety
        var runningSum = 0f
        for (tau in 1..<maxTau) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] *= tau / runningSum
        }

        // 3. Look for the first local minimum below the threshold
        var tauEstimate = -1
        for (tauInit in minTau until maxTau) {
            var tau = tauInit
            if (yinBuffer[tau] < threshold) {
                while (tau + 1 < maxTau && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++
                }
                tauEstimate = tau
                break
            }
        }

        if (tauEstimate == -1) return null // if not found, return null

        //4. Parabolic interpretation, an improvement
        val betterTau = parabolicInterpretation(yinBuffer, tauEstimate)
        return sampleRate.toFloat() / betterTau
    }

    private fun parabolicInterpretation(buffer: FloatArray, tau: Int): Float {
        // fits a parabola for a more precise pitch estimate

        if (tau <= 0 || tau >= buffer.size - 1) return tau.toFloat()

        val left = buffer[tau - 1]
        val centre = buffer[tau]
        val right = buffer[tau + 1]

        val denominator = 2f * (2f * centre - left - right)
        return if (denominator == 0f) {
            tau.toFloat()
        } else {
            tau+(right - left) / denominator

        }

    }

}