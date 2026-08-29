package com.bancoxyz.bank_batch_system;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Permite lanzar UN Job específico por línea de comandos, para poder generar
 * evidencia de ejecución de cada proceso por separado (requisito de la
 * entrega). Cada corrida usa un timestamp como parámetro para que Spring
 * Batch no la trate como una ejecución duplicada.
 *
 * Uso:
 *   ./mvnw spring-boot:run -Dspring-boot.run.arguments=--job=transacciones
 *   ./mvnw spring-boot:run -Dspring-boot.run.arguments=--job=intereses
 *   ./mvnw spring-boot:run -Dspring-boot.run.arguments=--job=anual
 */
@Component
public class BatchJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job reporteTransaccionesJob;
    private final Job calculoInteresesJob;
    private final Job estadosCuentaAnualesJob;

    public BatchJobRunner(JobLauncher jobLauncher,
            Job reporteTransaccionesJob,
            Job calculoInteresesJob,
            Job estadosCuentaAnualesJob) {
        this.jobLauncher = jobLauncher;
        this.reporteTransaccionesJob = reporteTransaccionesJob;
        this.calculoInteresesJob = calculoInteresesJob;
        this.estadosCuentaAnualesJob = estadosCuentaAnualesJob;
    }

    @Override
    public void run(String... args) throws Exception {
        String jobArg = Arrays.stream(args)
                .filter(a -> a.startsWith("--job="))
                .map(a -> a.substring("--job=".length()))
                .findFirst()
                .orElse(null);

        if (jobArg == null) {
            System.out.println("Ningún --job indicado. Usa --job=transacciones | --job=intereses | --job=anual");
            return;
        }

        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        switch (jobArg) {
            case "transacciones" -> jobLauncher.run(reporteTransaccionesJob, params);
            case "intereses" -> jobLauncher.run(calculoInteresesJob, params);
            case "anual" -> jobLauncher.run(estadosCuentaAnualesJob, params);
            default -> System.out.println("Job desconocido: " + jobArg);
        }
    }
}
