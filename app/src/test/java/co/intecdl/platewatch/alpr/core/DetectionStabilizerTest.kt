package co.intecdl.platewatch.alpr.core

import org.junit.Assert.*
import org.junit.Test

class DetectionStabilizerTest {
    @Test fun confirmsAfterThreeValidReadings() {
        val s = DetectionStabilizer()
        assertNull(s.add(PlateReading("ABC123", .90f, 0)))
        assertNull(s.add(PlateReading("ABC123", .92f, 500)))
        assertEquals("ABC123", s.add(PlateReading("ABC123", .95f, 900))?.plate)
        assertNull(s.add(PlateReading("ABC123", .99f, 1_100)))
    }
}
