package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.model.Transaccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransaccionProcessor implements ItemProcessor<Transaccion, Transaccion> {

    private static final Logger log = LoggerFactory.getLogger(TransaccionProcessor.class);

    @Override
    public Transaccion process(Transaccion t) {

        if (t == null) {
            return null;
        }

        // Descartar fecha vacía
        if (t.getFecha() == null) {
            log.warn("Fecha inválida en registro {}", t.getId());
            return null;
        }

        // Descartar monto vacío
        if (t.getMonto() == null) {
            log.warn("Monto nulo en registro {}", t.getId());
            return null;
        }

        // Descartar tipo inválido
        if (t.getTipo() == null ||
                (!t.getTipo().equalsIgnoreCase("debito")
                        && !t.getTipo().equalsIgnoreCase("credito"))) {

            log.warn("Tipo inválido en registro {}", t.getId());
            return null;
        }

        // Normalizar mayúsculas
        t.setTipo(t.getTipo().toUpperCase());

        return t;
    }
}