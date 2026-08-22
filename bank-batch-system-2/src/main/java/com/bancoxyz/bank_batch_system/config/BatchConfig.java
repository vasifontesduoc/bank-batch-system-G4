package com.bancoxyz.bank_batch_system.config;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import com.bancoxyz.bank_batch_system.processor.TransaccionProcessor;
import com.bancoxyz.bank_batch_system.writer.TransaccionWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import com.bancoxyz.bank_batch_system.writer.TransaccionWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BatchConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10);
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
            TaskExecutor taskExecutor) {

        return new StepBuilder("transaccionStep", jobRepository)
                .<Transaccion, Transaccion>chunk(5, transactionManager)
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job reporteTransaccionesJob(JobRepository jobRepository,
            Step transaccionStep) {

        return new JobBuilder("reporteTransaccionesJob", jobRepository)
                .start(transaccionStep)
                .build();
    }

    @Bean
    public Step respaldoStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> reader,
            TransaccionProcessor processor,
            FlatFileItemWriter<Transaccion> respaldoWriter) {

        return new StepBuilder("respaldoStep", jobRepository)
                .<Transaccion, Transaccion>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(respaldoWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .taskExecutor(taskExecutor())
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