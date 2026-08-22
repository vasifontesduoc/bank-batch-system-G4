package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnual, CuentaAnual> {

    private static final Logger log = LoggerFactory.getLogger(CuentaAnualProcessor.class);

    @Override
    public CuentaAnual process(CuentaAnual c) {

        if (c == null) {
            return null;
        }

        if (c.getMonto() == null) {
            log.warn("Monto nulo en la cuenta {}", c.getCuentaId());
            return null;
        }

        if (c.getTransaccion() != null) {
            c.setTransaccion(c.getTransaccion().toUpperCase());
        }

        log.info("Movimiento auditado: {}", c.getCuentaId());

        return c;
    }
}