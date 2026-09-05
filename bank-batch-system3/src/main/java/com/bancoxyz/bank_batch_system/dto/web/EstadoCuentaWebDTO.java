package com.bancoxyz.bank_batch_system.dto.web;

import java.util.List;

public record EstadoCuentaWebDTO(
        Long cuentaId,
        Integer cantidadMovimientos,
        Double totalDepositos,
        Double totalRetiros,
        Double totalOtros,
        Double saldoFinal,
        String fechaGeneracion,
        List<MovimientoWebDTO> movimientos) {
}
