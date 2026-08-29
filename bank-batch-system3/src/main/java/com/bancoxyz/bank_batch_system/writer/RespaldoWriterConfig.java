package com.bancoxyz.bank_batch_system.writer;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class RespaldoWriterConfig {

    @Bean
    public FlatFileItemWriter<Transaccion> respaldoWriter(@Value("${batch.backup.dir}") String backupDir)
            throws IOException {

        Files.createDirectories(Path.of(backupDir));
        Path archivo = Path.of(backupDir, "respaldo_cuentas.csv");

        return new FlatFileItemWriterBuilder<Transaccion>()
                .name("respaldoWriter")
                .resource(new FileSystemResource(archivo.toFile()))
                .delimited()
                .delimiter(",")
                .names("id", "fecha", "monto", "tipo")
                .headerCallback(writer -> writer.write("id,fecha,monto,tipo"))
                .build();
    }
}
