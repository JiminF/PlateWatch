package co.intecdl.platewatch.alpr.core

import org.junit.Assert.assertEquals
import org.junit.Test

class PlateNormalizerTest {
    private val normalizer = PlateNormalizer()
    @Test fun normalizesCommonFormats() {
        assertEquals("ABC123", normalizer.normalize("abc-123"))
        assertEquals("ABC123", normalizer.normalize(" ABC 123 "))
    }
}
