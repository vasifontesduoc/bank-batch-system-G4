package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnual, CuentaAnual> {

    @Override
    public CuentaAnual process(CuentaAnual item) {

        if (item.getMonto() < 0) {
            System.out.println("Movimiento detectado para auditoría -> Cuenta: " + item.getCuentaId());
        }

        item.setTransaccion(item.getTransaccion().toUpperCase());

        return item;
    }
}