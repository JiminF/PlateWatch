package co.intecdl.platewatch.alpr.core

import java.util.ArrayDeque

data class PlateReading(val plate: String, val confidence: Float, val timestampMillis: Long)
data class ConfirmedPlate(val plate: String, val confidence: Float, val confirmedAtMillis: Long)

class DetectionStabilizer(
    private val minimumConfidence: Float = 0.85f,
    private val requiredReadings: Int = 3,
    private val windowMillis: Long = 2_000,
    private val cooldownMillis: Long = 10_000
) {
    private val readings = ArrayDeque<PlateReading>()
    private val lastConfirmation = mutableMapOf<String, Long>()

    @Synchronized
    fun add(reading: PlateReading): ConfirmedPlate? {
        if (reading.confidence < minimumConfidence || reading.plate.isBlank()) return null
        while (readings.isNotEmpty() && reading.timestampMillis - readings.first.timestampMillis > windowMillis) readings.removeFirst()
        readings.addLast(reading)
        val matching = readings.filter { it.plate == reading.plate }
        if (matching.size < requiredReadings) return null
        val previous = lastConfirmation[reading.plate]
        if (previous != null && reading.timestampMillis - previous < cooldownMillis) return null
        lastConfirmation[reading.plate] = reading.timestampMillis
        return ConfirmedPlate(reading.plate, matching.maxOf { it.confidence }, reading.timestampMillis)
    }
}
