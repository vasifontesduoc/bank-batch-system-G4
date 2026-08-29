package com.bancoxyz.bank_batch_system.config;

import com.bancoxyz.bank_batch_system.exception.TransaccionInvalidaException;
import com.bancoxyz.bank_batch_system.listener.ResumenTransaccionesListener;
import com.bancoxyz.bank_batch_system.listener.TransaccionSkipListener;
import com.bancoxyz.bank_batch_system.model.Transaccion;
import com.bancoxyz.bank_batch_system.processor.TransaccionProcessor;
import com.bancoxyz.bank_batch_system.writer.TransaccionWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BatchConfig {

    @Bean
    public TaskExecutor taskExecutor(
            @Value("${batch.threads.core:3}") int corePoolSize,
            @Value("${batch.threads.max:5}") int maxPoolSize,
            @Value("${batch.threads.queue-capacity:10}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("batch-tx-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step transaccionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> transaccionReader,
            ItemProcessor<Transaccion, Transaccion> transaccionProcessor,
            TransaccionWriter transaccionWriter,
            TaskExecutor taskExecutor,
            TransaccionSkipListener transaccionSkipListener) {

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000L);

        return new StepBuilder("transaccionStep", jobRepository)
                .<Transaccion, Transaccion>chunk(5, transactionManager)
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                .faultTolerant()
                .skip(TransaccionInvalidaException.class)
                .skipLimit(600)
                .retry(TransientDataAccessException.class)
                .retryLimit(3)
                .backOffPolicy(backOffPolicy)
                .listener(transaccionSkipListener)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job reporteTransaccionesJob(JobRepository jobRepository,
            Step transaccionStep,
            ResumenTransaccionesListener resumenTransaccionesListener) {

        return new JobBuilder("reporteTransaccionesJob", jobRepository)
                .listener(resumenTransaccionesListener)
                .start(transaccionStep)
                .build();
    }

    // --- Job auxiliar de respaldo (backup del CSV validado, no reemplaza el
    // reporte a BD) ---

    @Bean
    public Step respaldoStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> reader,
            TransaccionProcessor processor,
            FlatFileItemWriter<Transaccion> respaldoWriter,
            TransaccionSkipListener transaccionSkipListener,
            TaskExecutor taskExecutor) {

        return new StepBuilder("respaldoStep", jobRepository)
                .<Transaccion, Transaccion>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(respaldoWriter)
                .faultTolerant()
                .skip(TransaccionInvalidaException.class)
                .skipLimit(600)
                .listener(transaccionSkipListener)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job respaldoCuentasJob(JobRepository jobRepository,
            Step respaldoStep) {

        return new JobBuilder("respaldoCuentasJob", jobRepository)
                .start(respaldoStep)
                .build();
    }
}