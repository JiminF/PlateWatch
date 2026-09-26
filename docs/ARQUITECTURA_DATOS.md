# Arquitectura de datos

- `WatchedPlateEntity`: una fila por placa vigilada normalizada.
- `ObservedPlateEntity`: resumen acumulado, primera y ultima observacion.
- `DetectionEntity`: evento individual e inmutable de cada deteccion confirmada.
- `AppSettingsEntity`: configuracion unica de la aplicacion.
- `PlateRepository`: transaccion atomica que actualiza el resumen observado y crea el evento individual.

La deduplicacion no elimina el historial. Agrupa el resumen en `observed_plates`, pero conserva cada evento en `detections`.
