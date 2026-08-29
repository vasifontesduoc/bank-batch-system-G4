package com.bancoxyz.bank_batch_system.listener;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import com.bancoxyz.bank_batch_system.processor.TransaccionSummaryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class TransaccionSkipListener implements SkipListener<Transaccion, Transaccion> {

    private static final Logger log = LoggerFactory.getLogger(TransaccionSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        TransaccionSummaryHolder.registrarRechazada();
        log.warn("Registro descartado al LEER el CSV: {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(Transaccion item, Throwable t) {
        TransaccionSummaryHolder.registrarRechazada();
        log.warn("Registro descartado al PROCESAR (id={}): {}", item.getId(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(Transaccion item, Throwable t) {
        TransaccionSummaryHolder.registrarRechazada();
        log.warn("Registro descartado al ESCRIBIR (id={}): {}", item.getId(), t.getMessage());
    }
}
