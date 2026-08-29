package com.bancoxyz.bank_batch_system.processor;

import java.util.concurrent.atomic.AtomicInteger;

public final class TransaccionSummaryHolder {

    private static final AtomicInteger procesadas = new AtomicInteger(0);
    private static final AtomicInteger anomalias = new AtomicInteger(0);
    private static final AtomicInteger rechazadas = new AtomicInteger(0);

    private TransaccionSummaryHolder() {
    }

    public static void reset() {
        procesadas.set(0);
        anomalias.set(0);
        rechazadas.set(0);
    }

    public static void registrarProcesada(boolean esAnomalia) {
        procesadas.incrementAndGet();
        if (esAnomalia) {
            anomalias.incrementAndGet();
        }
    }

    public static void registrarRechazada() {
        rechazadas.incrementAndGet();
    }

    public static int getProcesadas() {
        return procesadas.get();
    }

    public static int getAnomalias() {
        return anomalias.get();
    }

    public static int getRechazadas() {
        return rechazadas.get();
    }
}
