package com.bancoxyz.bank_batch_system.writer;

import com.bancoxyz.bank_batch_system.model.Interes;
import com.bancoxyz.bank_batch_system.repository.InteresRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class InteresWriter implements ItemWriter<Interes> {

    private final InteresRepository repository;

    public InteresWriter(InteresRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends Interes> items) {
        repository.saveAll(items.getItems());
    }
}