package com.bancoxyz.bank_batch_system.dto.web;

public record TransaccionWebDTO(Long id, String fecha, Double monto, String tipo, boolean anomalia) {
}
