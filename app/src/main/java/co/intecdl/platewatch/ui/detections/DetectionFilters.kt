package co.intecdl.platewatch.ui.detections

import java.time.LocalDate
import java.time.ZoneId

enum class MatchFilter(val label: String, val databaseValue: Boolean?) {
    ALL("Todas", null),
    MATCHED("Coincidencias", true),
    NOT_MATCHED("No coincidentes", false)
}

enum class DateFilter(val label: String) {
    ALL("Todo"),
    TODAY("Hoy"),
    LAST_7_DAYS("7 dias"),
    LAST_30_DAYS("30 dias")
}

data class UtcRange(val start: Long?, val endExclusive: Long?)

fun DateFilter.toUtcRange(
    today: LocalDate = LocalDate.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
): UtcRange {
    if (this == DateFilter.ALL) return UtcRange(null, null)
    val startDate = when (this) {
        DateFilter.TODAY -> today
        DateFilter.LAST_7_DAYS -> today.minusDays(6)
        DateFilter.LAST_30_DAYS -> today.minusDays(29)
        DateFilter.ALL -> today
    }
    return UtcRange(
        start = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli(),
        endExclusive = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    )
}
