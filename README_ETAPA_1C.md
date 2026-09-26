# PlateWatch - Actualizacion Etapa 1C

Esta entrega es una actualizacion incremental para aplicar sobre la Etapa 1B.

## Integrar

1. Copiar el contenido de esta carpeta sobre la raiz `PlateWatch`.
2. En la pantalla de seleccion interna, insertar `UsbCameraSection()` debajo de la lista de camaras internas.
3. Verificar que la Etapa 1B conserve `CameraSource`, `CameraCapabilities`, `CameraFrame` y `CameraConnectionState`.
4. Sincronizar y compilar en Android Studio.

## Alcance

- Inventario real de dispositivos USB.
- Identificacion probable UVC mediante interfaces de clase video.
- Solicitud de permiso USB.
- Recepcion dinamica de conexion, desconexion y resultado del permiso.
- Interfaz Compose para mostrar dispositivos reales.
- Frontera `UsbCameraSource` sin preview falso.

El preview UVC no se marca como terminado. Requiere integrar y validar AUSBC en el Galaxy A24.
