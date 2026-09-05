package com.bancoxyz.bank_batch_system.service;

import com.bancoxyz.bank_batch_system.dto.mobile.SaldoMobileDTO;
import com.bancoxyz.bank_batch_system.dto.mobile.TransaccionMobileDTO;
import com.bancoxyz.bank_batch_system.exception.CuentaNoEncontradaException;
import com.bancoxyz.bank_batch_system.repository.EstadoCuentaAnualRepository;
import com.bancoxyz.bank_batch_system.repository.TransaccionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileBffService {

    private final EstadoCuentaAnualRepository estadoRepository;
    private final TransaccionRepository transaccionRepository;

    public MobileBffService(EstadoCuentaAnualRepository estadoRepository,
            TransaccionRepository transaccionRepository) {
        this.estadoRepository = estadoRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public SaldoMobileDTO obtenerSaldo(Long cuentaId) {
        var estado = estadoRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
        return new SaldoMobileDTO(estado.getCuentaId(), estado.getSaldoFinal());
    }

    public List<TransaccionMobileDTO> transaccionesRecientes(int limite) {
        return transaccionRepository
                .findAll(PageRequest.of(0, limite, Sort.by("id").descending()))
                .stream()
                .map(t -> new TransaccionMobileDTO(t.getFecha(), t.getMonto(), t.getTipo()))
                .toList();
    }
}
