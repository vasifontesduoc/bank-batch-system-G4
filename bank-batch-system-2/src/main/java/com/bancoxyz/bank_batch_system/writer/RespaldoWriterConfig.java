package com.bancoxyz.bank_batch_system.writer;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class RespaldoWriterConfig {

    @Bean
    public FlatFileItemWriter<Transaccion> respaldoWriter() {
        return new FlatFileItemWriterBuilder<Transaccion>()
                .name("respaldoWriter")
                .resource(new FileSystemResource("src/main/resources/output/respaldo_cuentas.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "cuenta", "monto", "tipo", "fecha")
                .headerCallback(writer -> writer.write("id,cuenta,monto,tipo,fecha"))
                .build();
    }
}