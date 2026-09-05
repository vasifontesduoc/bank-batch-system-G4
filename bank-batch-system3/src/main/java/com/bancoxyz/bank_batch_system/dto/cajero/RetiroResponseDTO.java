package com.bancoxyz.bank_batch_system.dto.cajero;

public record RetiroResponseDTO(
        Long cuentaId,
        Double montoRetirado,
        Double saldoAnterior,
        Double saldoNuevo,
        String fecha) {
}
