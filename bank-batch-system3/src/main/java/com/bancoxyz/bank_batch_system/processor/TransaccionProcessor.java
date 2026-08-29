package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.exception.TransaccionInvalidaException;
import com.bancoxyz.bank_batch_system.model.Transaccion;
import com.bancoxyz.bank_batch_system.util.FechaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TransaccionProcessor implements ItemProcessor<Transaccion, Transaccion> {

    private static final Logger log = LoggerFactory.getLogger(TransaccionProcessor.class);
    private static final Set<String> TIPOS_VALIDOS = Set.of("DEBITO", "CREDITO");
    private static final double UMBRAL_ANOMALIA = 3000.0;

    @Override
    public Transaccion process(Transaccion t) {

        if (t == null) {
            return null;
        }

        if (t.getId() == null) {
            throw new TransaccionInvalidaException("Transacción sin id (registro descartado)");
        }

        if (!FechaUtils.esFechaValida(t.getFecha())) {
            throw new TransaccionInvalidaException("Fecha inválida '" + t.getFecha() + "' en transacción " + t.getId());
        }

        if (t.getMonto() == null) {
            throw new TransaccionInvalidaException("Monto nulo o no numérico en transacción " + t.getId());
        }

        if (t.getTipo() == null || !TIPOS_VALIDOS.contains(t.getTipo().trim().toUpperCase())) {
            throw new TransaccionInvalidaException(
                    "Tipo de transacción inválido '" + t.getTipo() + "' en registro " + t.getId());
        }

        t.setTipo(t.getTipo().trim().toUpperCase());

        boolean esAnomalia = t.getMonto() <= 0.0 || t.getMonto() >= UMBRAL_ANOMALIA;
        if (esAnomalia) {
            log.warn("ANOMALÍA detectada en transacción {}: monto={} tipo={}", t.getId(), t.getMonto(), t.getTipo());
        }

        TransaccionSummaryHolder.registrarProcesada(esAnomalia);

        return t;
    }
}
