package com.bancoxyz.bank_batch_system.writer;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import com.bancoxyz.bank_batch_system.repository.CuentaAnualRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class CuentaAnualWriter implements ItemWriter<CuentaAnual> {

    private final CuentaAnualRepository repository;

    public CuentaAnualWriter(CuentaAnualRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends CuentaAnual> items) {
        repository.saveAll(items.getItems());
    }
}
