package com.bancoxyz.bank_batch_system.processor;

import com.bancoxyz.bank_batch_system.exception.InteresInvalidoException;
import com.bancoxyz.bank_batch_system.model.Interes;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Component
public class InteresProcessor implements ItemProcessor<Interes, Interes> {

    private static final Logger log = LoggerFactory.getLogger(InteresProcessor.class);

    private static final double TASA_AHORRO = 1.05;
    private static final double TASA_PRESTAMO = 1.02;
    private static final double TASA_HIPOTECA = 1.03;
    private static final Set<String> TIPOS_VALIDOS = Set.of("AHORRO", "PRESTAMO", "PRÉSTAMO", "HIPOTECA");

    @Override
    public Interes process(Interes i) {

        if (i == null) {
            return null;
        }

        if (i.getCuentaId() == null) {
            throw new InteresInvalidoException("Registro de interés sin cuentaId");
        }

        if (i.getSaldo() == null) {
            throw new InteresInvalidoException("Saldo nulo o no numérico en la cuenta " + i.getCuentaId());
        }

        if (i.getSaldo() < 0) {
            throw new InteresInvalidoException("Saldo negativo inconsistente en la cuenta " + i.getCuentaId());
        }

        if (i.getEdad() != null && (i.getEdad() < 0 || i.getEdad() > 120)) {
            throw new InteresInvalidoException("Edad fuera de rango (" + i.getEdad() + ") en la cuenta " + i.getCuentaId());
        }

        if (i.getTipo() == null || !TIPOS_VALIDOS.contains(i.getTipo().trim().toUpperCase())) {
            throw new InteresInvalidoException("Tipo de cuenta inválido '" + i.getTipo() + "' en la cuenta " + i.getCuentaId());
        }

        double saldo = i.getSaldo();

        switch (i.getTipo().trim().toUpperCase()) {
            case "AHORRO" -> saldo = saldo * TASA_AHORRO;
            case "PRESTAMO", "PRÉSTAMO" -> saldo = saldo * TASA_PRESTAMO;
            case "HIPOTECA" -> saldo = saldo * TASA_HIPOTECA;
        }

        i.setSaldo(saldo);

        log.info("Interés aplicado a cuenta {} (tipo={}): nuevo saldo={}", i.getCuentaId(), i.getTipo(), saldo);

        return i;
    }
}
