package com.bancoxyz.bank_batch_system.reader;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class CuentaAnualReader {

    @Bean
    public FlatFileItemReader<CuentaAnual> cuentaAnualItemReader() {

        return new FlatFileItemReaderBuilder<CuentaAnual>()
                .name("cuentaAnualReader")
                .resource(new ClassPathResource("input/cuentas_anuales.csv"))
                .delimited()
                .names("cuenta_id", "fecha", "transaccion", "monto", "descripcion")
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> {
                    CuentaAnual cuenta = new CuentaAnual();
                    cuenta.setCuentaId(fieldSet.readLong("cuenta_id"));
                    cuenta.setFecha(fieldSet.readString("fecha"));
                    cuenta.setTransaccion(fieldSet.readString("transaccion"));

                    String montoStr = fieldSet.readString("monto");
                    try {
                        cuenta.setMonto(montoStr == null || montoStr.isBlank()
                                ? null
                                : Double.parseDouble(montoStr.trim()));
                    } catch (NumberFormatException e) {
                        cuenta.setMonto(null);
                    }

                    cuenta.setDescripcion(fieldSet.readString("descripcion"));
                    return cuenta;
                })
                .build();
    }
}
