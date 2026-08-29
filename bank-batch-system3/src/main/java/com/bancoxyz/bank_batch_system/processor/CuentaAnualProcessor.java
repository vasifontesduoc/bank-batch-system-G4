package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.exception.CuentaInvalidaException;
import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import com.bancoxyz.bank_batch_system.util.FechaUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.Set;

@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnual, CuentaAnual> {

    private static final Logger log = LoggerFactory.getLogger(CuentaAnualProcessor.class);
    private static final Set<String> TIPOS_VALIDOS = Set.of("DEPOSITO", "RETIRO", "COMPRA", "PAGO");

    @Override
    public CuentaAnual process(CuentaAnual c) {

        if (c == null) {
            return null;
        }

        if (c.getCuentaId() == null) {
            throw new CuentaInvalidaException("Movimiento anual sin cuentaId");
        }

        if (c.getMonto() == null) {
            throw new CuentaInvalidaException("Monto nulo o no numérico en la cuenta " + c.getCuentaId());
        }

        if (!FechaUtils.esFechaValida(c.getFecha())) {
            throw new CuentaInvalidaException("Fecha inválida '" + c.getFecha() + "' en la cuenta " + c.getCuentaId());
        }

        if (c.getTransaccion() == null || c.getTransaccion().isBlank()) {
            throw new CuentaInvalidaException("Tipo de movimiento faltante en la cuenta " + c.getCuentaId());
        }
        
        String tipoNormalizado = quitarAcentos(c.getTransaccion().trim()).toUpperCase();

        if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
            throw new CuentaInvalidaException(
                    "Tipo de movimiento desconocido '" + c.getTransaccion() + "' en la cuenta " + c.getCuentaId());
        }

        c.setTransaccion(tipoNormalizado);

        if (c.getMonto() == 0.0) {
            log.warn("ANOMALÍA: movimiento con monto 0 en cuenta {}", c.getCuentaId());
        }

        log.info("Movimiento auditado: cuenta={} tipo={} monto={}", c.getCuentaId(), tipoNormalizado, c.getMonto());

        return c;
    }

    private String quitarAcentos(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{M}", "");
    }
}
