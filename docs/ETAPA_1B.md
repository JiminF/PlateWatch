# Etapa 1B
`CameraXSession` une `Preview` e `ImageAnalysis` al ciclo de vida. La estrategia `KEEP_ONLY_LATEST` descarta frames viejos cuando el analizador esta ocupado. Cada `ImageProxy` se cierra para devolver el buffer a la camara. Las metricas provienen de frames reales.
