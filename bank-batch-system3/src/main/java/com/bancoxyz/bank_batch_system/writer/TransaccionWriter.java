package com.bancoxyz.bank_batch_system.writer;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import com.bancoxyz.bank_batch_system.repository.TransaccionRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class TransaccionWriter implements ItemWriter<Transaccion> {

    private final TransaccionRepository repository;

    public TransaccionWriter(TransaccionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends Transaccion> items) {
        repository.saveAll(items.getItems());
    }
}