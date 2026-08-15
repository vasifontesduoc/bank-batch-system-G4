package com.bancoxyz.bank_batch_system.reader;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class TransaccionReader {

    @Bean
    public FlatFileItemReader<Transaccion> reader() {
        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader")
                .resource(new ClassPathResource("input/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "cuenta", "monto", "tipo", "fecha")
                .targetType(Transaccion.class)
                .build();
    }
}