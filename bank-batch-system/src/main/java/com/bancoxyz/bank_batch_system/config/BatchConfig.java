package com.bancoxyz.bank_batch_system.config;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import com.bancoxyz.bank_batch_system.processor.TransaccionProcessor;
import com.bancoxyz.bank_batch_system.writer.TransaccionWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Step transaccionStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> reader,
            TransaccionProcessor processor,
            TransaccionWriter writer) {

        return new StepBuilder("transaccionStep", jobRepository)
                .<Transaccion, Transaccion>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
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
                .<Transaccion, Transaccion>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(respaldoWriter)
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