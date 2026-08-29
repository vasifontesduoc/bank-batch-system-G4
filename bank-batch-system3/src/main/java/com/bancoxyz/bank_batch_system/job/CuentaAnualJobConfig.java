package com.bancoxyz.bank_batch_system.job;

import com.bancoxyz.bank_batch_system.exception.CuentaInvalidaException;
import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import com.bancoxyz.bank_batch_system.tasklet.EstadoCuentaAnualTasklet;
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
import org.springframework.dao.TransientDataAccessException;
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
                                .skip(CuentaInvalidaException.class)
                                .skipLimit(150)
                                .retry(TransientDataAccessException.class)
                                .retryLimit(3)
                                .taskExecutor(taskExecutor)
                                .build();
        }

        @Bean
        public Step compilarEstadoAnualStep(
                        JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        EstadoCuentaAnualTasklet estadoCuentaAnualTasklet) {

                return new StepBuilder("compilarEstadoAnualStep", jobRepository)
                                .tasklet(estadoCuentaAnualTasklet, transactionManager)
                                .build();
        }

        @Bean
        public Job estadosCuentaAnualesJob(
                        JobRepository jobRepository,
                        Step cuentaAnualStep,
                        Step compilarEstadoAnualStep) {

                return new JobBuilder("estadosCuentaAnualesJob", jobRepository)
                                .start(cuentaAnualStep)
                                .next(compilarEstadoAnualStep)
                                .build();
        }
}
