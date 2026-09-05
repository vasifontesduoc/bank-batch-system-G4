package com.bancoxyz.bank_batch_system.controller;

import com.bancoxyz.bank_batch_system.dto.cajero.RetiroRequestDTO;
import com.bancoxyz.bank_batch_system.dto.cajero.RetiroResponseDTO;
import com.bancoxyz.bank_batch_system.dto.cajero.SaldoCajeroDTO;
import com.bancoxyz.bank_batch_system.service.CajeroBffService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cajero")
public class CajeroBffController {

    private final CajeroBffService service;

    public CajeroBffController(CajeroBffService service) {
        this.service = service;
    }

    @GetMapping("/cuentas/{cuentaId}/saldo")
    public SaldoCajeroDTO saldo(@PathVariable Long cuentaId) {
        return service.consultarSaldo(cuentaId);
    }

    @PostMapping("/cuentas/{cuentaId}/retiro")
    public RetiroResponseDTO retirar(@PathVariable Long cuentaId, @Valid @RequestBody RetiroRequestDTO request) {
        return service.retirar(cuentaId, request.monto());
    }
}
