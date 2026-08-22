package com.bancoxyz.bank_batch_system.job;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import com.bancoxyz.bank_batch_system.writer.CuentaAnualWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CuentaAnualJobConfig {

    @Bean
    public Step cuentaAnualStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CuentaAnual> cuentaAnualReader,
            ItemProcessor<CuentaAnual, CuentaAnual> cuentaAnualProcessor,
            CuentaAnualWriter cuentaAnualWriter,
            TaskExecutor taskExecutor) {

        return new StepBuilder("cuentaAnualStep", jobRepository)
                .<CuentaAnual, CuentaAnual>chunk(5, transactionManager)
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job estadosCuentaAnualesJob(
            JobRepository jobRepository,
            Step cuentaAnualStep) {

        return new JobBuilder("estadosCuentaAnualesJob", jobRepository)
                .start(cuentaAnualStep)
                .build();
    }
}
