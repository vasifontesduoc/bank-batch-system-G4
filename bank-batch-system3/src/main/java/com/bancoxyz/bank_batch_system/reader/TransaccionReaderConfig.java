package com.bancoxyz.bank_batch_system.reader;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class TransaccionReaderConfig {

    @Bean(name = "transaccionReader")
    public FlatFileItemReader<Transaccion> transaccionReader() {

        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader")
                .resource(new ClassPathResource("input/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .fieldSetMapper(fieldSet -> {
                    Transaccion t = new Transaccion();
                    t.setId(fieldSet.readLong("id"));
                    t.setFecha(fieldSet.readString("fecha"));

                    String montoStr = fieldSet.readString("monto");
                    try {
                        t.setMonto(montoStr == null || montoStr.isBlank()
                                ? null
                                : Double.parseDouble(montoStr.trim()));
                    } catch (NumberFormatException e) {
                        t.setMonto(null);
                    }

                    t.setTipo(fieldSet.readString("tipo"));
                    return t;
                })
                .build();
    }
}
