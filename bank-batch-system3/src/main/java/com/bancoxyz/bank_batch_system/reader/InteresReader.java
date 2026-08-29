package com.bancoxyz.bank_batch_system.reader;

import com.bancoxyz.bank_batch_system.model.Interes;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class InteresReader {

    @Bean
    public FlatFileItemReader<Interes> interesItemReader() {

        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesReader")
                .resource(new ClassPathResource("input/intereses.csv"))
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .linesToSkip(1)
                .targetType(Interes.class)
                .build();
    }
}