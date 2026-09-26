package co.intecdl.platewatch.alpr.core
import org.junit.Assert.*
import org.junit.Test
class CoreTests{
 @Test fun normalizes(){assertEquals("ABC123",PlateNormalizer().normalize(" abc-123 "))}
 @Test fun confirmsAndCoolsDown(){val s=DetectionStabilizer();assertNull(s.add(PlateReading("ABC123",.90f,0)));assertNull(s.add(PlateReading("ABC123",.91f,500)));assertNotNull(s.add(PlateReading("ABC123",.95f,900)));assertNull(s.add(PlateReading("ABC123",.99f,1000)))}
}
