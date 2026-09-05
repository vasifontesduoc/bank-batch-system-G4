package com.bancoxyz.bank_batch_system.controller;

import com.bancoxyz.bank_batch_system.dto.web.EstadoCuentaWebDTO;
import com.bancoxyz.bank_batch_system.dto.web.InteresWebDTO;
import com.bancoxyz.bank_batch_system.dto.web.TransaccionWebDTO;
import com.bancoxyz.bank_batch_system.service.WebBffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/web")
public class WebBffController {

    private final WebBffService service;

    public WebBffController(WebBffService service) {
        this.service = service;
    }

    @GetMapping("/cuentas/{cuentaId}/estado-anual")
    public EstadoCuentaWebDTO estadoAnual(@PathVariable Long cuentaId) {
        return service.obtenerEstadoCuenta(cuentaId);
    }

    @GetMapping("/transacciones")
    public List<TransaccionWebDTO> transacciones() {
        return service.listarTransacciones();
    }

    @GetMapping("/cuentas/{cuentaId}/intereses")
    public List<InteresWebDTO> intereses(@PathVariable Long cuentaId) {
        return service.historialIntereses(cuentaId);
    }
}