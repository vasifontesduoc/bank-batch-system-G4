package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.model.Interes;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class InteresProcessor implements ItemProcessor<Interes, Interes> {

    @Override
    public Interes process(Interes item) {

        double nuevoSaldo = item.getSaldo();

        switch (item.getTipo().toLowerCase()) {

            case "ahorro":
                nuevoSaldo *= 1.03;
                break;

            case "prestamo":
                nuevoSaldo *= 1.08;
                break;

            case "hipoteca":
                nuevoSaldo *= 1.05;
                break;
        }

        item.setSaldo(nuevoSaldo);

        return item;
    }
}