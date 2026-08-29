# Bank Batch System — Banco XYZ

Migración de procesos batch legacy del Banco XYZ a **Spring Batch** — Semana 3, Desarrollo Backend III (PBY2203).

## Objetivo

Modernizar tres procesos batch del banco con manejo robusto de errores y procesamiento paralelo:

1. **Reporte de Transacciones Diarias** — valida transacciones, detecta anomalías y genera un resumen.
2. **Cálculo de Intereses Mensuales** — aplica tasa según tipo de cuenta (ahorro/préstamo/hipoteca) y actualiza el saldo.
3. **Estados de Cuenta Anuales** — compila los movimientos del año por cuenta y genera un informe de auditoría.

## Estructura del código
config/ -> Jobs y Steps (transacciones, intereses)
job/ -> Job de cuentas anuales (2 steps)
reader/ -> Lectura de cada CSV
processor/ -> Validación, normalización y detección de anomalías
writer/ -> Persistencia en MySQL
tasklet/ -> Compila el estado de cuenta anual consolidado
listener/ -> Resumen de ejecución y trazabilidad de registros descartados
exception/ -> Excepciones de negocio usadas por las políticas de skip
model/ repository/ -> Entidades JPA y repositorios


Datos basados en: https://github.com/KariVillagran/bank_legacy_data

## Decisiones técnicas

**Tolerancia a fallos:** cada Step usa `.faultTolerant()` con excepciones de negocio propias (`TransaccionInvalidaException`, `InteresInvalidoException`, `CuentaInvalidaException`) para registros con datos inválidos (skip, hasta 600 en transacciones/intereses y 150 en cuentas anuales, dado que el dataset real trae ~48% de registros deliberadamente inválidos). Los errores técnicos/transitorios de BD se reintentan hasta 3 veces con backoff, en vez de descartarse.

**Anomalías:** se marcan (no se descartan) transacciones con monto ≤0 o ≥3000, quedando registradas en el resumen final del Job.

**Escalamiento:** procesamiento multi-hilo (`ThreadPoolTaskExecutor`), configurable por `application.properties`. Se comparó 1 vs 3 vs 8 hilos sobre el mismo Job (~1000 registros):

| Hilos | Tiempo |
|---|---|
| 1 | 697 ms |
| **3** | **641 ms** |
| 8 | 662 ms |

3 hilos resultó ser el óptimo — 8 hilos no mejora el resultado por el overhead de coordinación frente al volumen de datos.

**Rutas externas:** los resultados (`data/output/`, `data/backup/`) se generan fuera de `src/main/resources`, configurables vía `application.properties`, para no mezclar artefactos de ejecución con el jar empaquetado.

# Ejecutar cada Job por separado
mvn spring-boot:run -Dspring-boot.run.arguments=--job=transacciones
mvn spring-boot:run -Dspring-boot.run.arguments=--job=intereses
mvn spring-boot:run -Dspring-boot.run.arguments=--job=anual
```

Ajusta usuario/contraseña de MySQL en `src/main/resources/application.properties` si no usas `root/password`.

## Evidencia de ejecución

Capturas de consola de cada Job, comparación de hilos y consultas en MySQL con los datos almacenados están incluidas en el documento de entrega adjunto.
