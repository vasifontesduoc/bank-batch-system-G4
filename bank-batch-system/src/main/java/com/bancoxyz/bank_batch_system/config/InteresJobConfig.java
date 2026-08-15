package com.bancoxyz.bank_batch_system.config;

import com.bancoxyz.bank_batch_system.model.Interes;
import com.bancoxyz.bank_batch_system.processor.InteresProcessor;
import com.bancoxyz.bank_batch_system.writer.InteresWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InteresJobConfig {

    @Bean
    public Step interesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Interes> interesItemReader,
            InteresProcessor interesProcessor,
            InteresWriter interesWriter) {

        return new StepBuilder("interesStep", jobRepository)
                .<Interes, Interes>chunk(3, transactionManager)
                .reader(interesItemReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .build();
    }

    @Bean
    public Job calculoInteresesJob(
            JobRepository jobRepository,
            Step interesStep) {

        return new JobBuilder("calculoInteresesJob", jobRepository)
                .start(interesStep)
                .build();
    }
}