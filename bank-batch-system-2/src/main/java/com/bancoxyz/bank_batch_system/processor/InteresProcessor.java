package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.model.Interes;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InteresProcessor implements ItemProcessor<Interes, Interes> {

    private static final Logger log = LoggerFactory.getLogger(InteresProcessor.class);

    @Override
    public Interes process(Interes i) {

        if (i == null) {
            return null;
        }

        if (i.getSaldo() == null) {
            log.warn("Saldo nulo en la cuenta {}", i.getCuentaId());
            return null;
        }

        Double saldo = i.getSaldo();

        switch (i.getTipo().toLowerCase()) {
            case "ahorro":
                saldo = saldo * 1.05;
                break;

            case "prestamo":
                saldo = saldo * 1.02;
                break;

            default:
                saldo = saldo * 1.01;
                break;
        }

        i.setSaldo(saldo);

        log.info("Interés aplicado a cuenta {}", i.getCuentaId());

        return i;
    }
}