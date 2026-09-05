package com.bancoxyz.bank_batch_system.exception;

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(Long cuentaId) {
        super("No existe estado de cuenta para la cuenta " + cuentaId);
    }
}