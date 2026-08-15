package com.bancoxyz.bank_batch_system.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.bancoxyz.bank_batch_system.model.Transaccion;

@Component
public class TransaccionProcessor implements ItemProcessor<Transaccion, Transaccion> {

    @Override
    public Transaccion process(Transaccion item) {

        // Descarta montos negativos
        if (item.getMonto() < 0) {
            System.out.println("Transacción descartada: monto negativo -> " + item.getId());
            return null;
        }

        // Convierte el tipo a mayúsculas
        item.setTipo(item.getTipo().toUpperCase());

        return item;
    }
}