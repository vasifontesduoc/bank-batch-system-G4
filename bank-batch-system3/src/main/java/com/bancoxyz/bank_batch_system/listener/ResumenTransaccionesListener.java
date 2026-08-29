package com.bancoxyz.bank_batch_system.listener;

import com.bancoxyz.bank_batch_system.processor.TransaccionSummaryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ResumenTransaccionesListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(ResumenTransaccionesListener.class);

    private final String outputDir;

    public ResumenTransaccionesListener(@Value("${batch.output.dir}") String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        TransaccionSummaryHolder.reset();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        int procesadas = TransaccionSummaryHolder.getProcesadas();
        int anomalias = TransaccionSummaryHolder.getAnomalias();
        int rechazadas = TransaccionSummaryHolder.getRechazadas();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        log.info("========== RESUMEN REPORTE DE TRANSACCIONES DIARIAS ==========");
        log.info("Estado del Job          : {}", jobExecution.getStatus());
        log.info("Transacciones procesadas: {}", procesadas);
        log.info("Anomalías detectadas    : {}", anomalias);
        log.info("Transacciones rechazadas: {}", rechazadas);
        log.info("================================================================");

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            try {
                Files.createDirectories(Path.of(outputDir));
            } catch (IOException e) {
                log.error("No se pudo crear el directorio de salida {}: {}", outputDir, e.getMessage());
                return;
            }

            Path archivo = Path.of(outputDir, "resumen_transacciones_" + timestamp + ".csv");
            try (FileWriter writer = new FileWriter(archivo.toFile())) {
                writer.write("metrica,valor\n");
                writer.write("transacciones_procesadas," + procesadas + "\n");
                writer.write("anomalias_detectadas," + anomalias + "\n");
                writer.write("transacciones_rechazadas," + rechazadas + "\n");
                writer.write("fecha_generacion," + timestamp + "\n");
                log.info("Resumen escrito en {}", archivo.toAbsolutePath());
            } catch (IOException e) {
                log.error("No se pudo escribir el archivo de resumen: {}", e.getMessage());
            }
        }
    }
}
