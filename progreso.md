# Progreso de PlateWatch

## 26/09/2026 - Etapa 1C, infraestructura USB

- [x] Inventario real con UsbManager.
- [x] Inspeccion de interfaces USB Video Class.
- [x] Descriptor estable VID, PID y deviceId.
- [x] Solicitud de permiso USB mediante PendingIntent explicito al paquete.
- [x] Receiver dinamico no exportado en Android moderno.
- [x] Eventos ATTACHED, DETACHED, PERMISSION_GRANTED y PERMISSION_DENIED.
- [x] Liberacion segura del BroadcastReceiver.
- [x] Interfaz Compose para dispositivos USB reales.
- [x] Adaptador `UsbCameraSource` desacoplado.
- [ ] Integrar backend AUSBC y preview UVC real.
- [ ] Obtener frames YUV/RGBA desde UVC.
- [ ] Validar reconexion con la camara USB exacta.
- [ ] Validar alimentacion OTG en Galaxy A24.

## Etapa 2A - Persistencia local Room

- [x] Entidades de placas vigiladas, observadas, detecciones y configuracion.
- [x] Indices para placa, fecha, coincidencia y estado activo.
- [x] DAO con Flow y operaciones suspendidas.
- [x] Base de datos local version 1.
- [x] Conversor para modo de funcionamiento.
- [x] Repositorio transaccional para observacion + evento.
- [x] Normalizacion antes de escribir o comparar.
- [x] Configuracion inicial de confianza, lecturas, ventana y cooldown.
- [ ] Integrar Gradle y validar KSP/Room en Android Studio.
- [ ] Crear pantallas CRUD de placas vigiladas.
- [ ] Crear pantallas de observadas e historial.
- [ ] Agregar pruebas instrumentadas de Room y migraciones.

## Integridad

La base no contiene datos precargados ni detecciones simuladas.

No se simula preview USB. Hasta incorporar el backend UVC, el estado visible es `UVC BACKEND NOT AVAILABLE`.
