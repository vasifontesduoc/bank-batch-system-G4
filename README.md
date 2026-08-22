# bank-batch-system-G4

Proyecto desarrollado en **Spring Batch** para modernizar procesos batch del Banco XYZ.

## Tecnologías

- Java 17
- Spring Boot
- Spring Batch
- MySQL
- Maven

## Procesos implementados

- Reporte de Transacciones Diarias
- Cálculo de Intereses Mensuales
- Generación de Estados de Cuenta Anuales

## Características

- Lectura de archivos CSV.
- Procesamiento por chunks de 5 registros.
- Ejecución paralela con 3 hilos.
- Validación de datos con `ItemProcessor`.
- Persistencia de resultados en MySQL.
- Tolerancia a fallos mediante políticas de `skip`.
