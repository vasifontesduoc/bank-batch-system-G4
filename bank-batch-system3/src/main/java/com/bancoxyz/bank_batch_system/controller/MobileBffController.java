package com.bancoxyz.bank_batch_system.controller;

import com.bancoxyz.bank_batch_system.dto.mobile.SaldoMobileDTO;
import com.bancoxyz.bank_batch_system.dto.mobile.TransaccionMobileDTO;
import com.bancoxyz.bank_batch_system.service.MobileBffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mobile")
public class MobileBffController {

    private final MobileBffService service;

    public MobileBffController(MobileBffService service) {
        this.service = service;
    }

    @GetMapping("/cuentas/{cuentaId}/saldo")
    public SaldoMobileDTO saldo(@PathVariable Long cuentaId) {
        return service.obtenerSaldo(cuentaId);
    }

    @GetMapping("/transacciones/recientes")
    public List<TransaccionMobileDTO> recientes(@RequestParam(defaultValue = "5") int limite) {
        return service.transaccionesRecientes(limite);
    }
}
