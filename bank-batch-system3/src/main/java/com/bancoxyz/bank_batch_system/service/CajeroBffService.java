package com.bancoxyz.bank_batch_system.service;

import com.bancoxyz.bank_batch_system.dto.cajero.RetiroResponseDTO;
import com.bancoxyz.bank_batch_system.dto.cajero.SaldoCajeroDTO;
import com.bancoxyz.bank_batch_system.exception.CuentaNoEncontradaException;
import com.bancoxyz.bank_batch_system.exception.FondosInsuficientesException;
import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import com.bancoxyz.bank_batch_system.model.EstadoCuentaAnual;
import com.bancoxyz.bank_batch_system.repository.CuentaAnualRepository;
import com.bancoxyz.bank_batch_system.repository.EstadoCuentaAnualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CajeroBffService {

    private final EstadoCuentaAnualRepository estadoRepository;
    private final CuentaAnualRepository movimientosRepository;

    public CajeroBffService(EstadoCuentaAnualRepository estadoRepository,
            CuentaAnualRepository movimientosRepository) {
        this.estadoRepository = estadoRepository;
        this.movimientosRepository = movimientosRepository;
    }

    public SaldoCajeroDTO consultarSaldo(Long cuentaId) {
        EstadoCuentaAnual estado = buscarEstado(cuentaId);
        return new SaldoCajeroDTO(estado.getCuentaId(), estado.getSaldoFinal());
    }

    @Transactional
    public RetiroResponseDTO retirar(Long cuentaId, Double monto) {
        EstadoCuentaAnual estado = buscarEstado(cuentaId);
        double saldoActual = estado.getSaldoFinal();

        if (monto > saldoActual) {
            throw new FondosInsuficientesException(cuentaId, saldoActual, monto);
        }

        double saldoNuevo = saldoActual - monto;
        String hoy = LocalDate.now().toString();

        estado.setSaldoFinal(saldoNuevo);
        estado.setTotalRetiros(estado.getTotalRetiros() + monto);
        estado.setCantidadMovimientos(estado.getCantidadMovimientos() + 1);
        estadoRepository.save(estado);

        CuentaAnual movimiento = new CuentaAnual();
        movimiento.setCuentaId(cuentaId);
        movimiento.setFecha(hoy);
        movimiento.setTransaccion("RETIRO");
        movimiento.setMonto(-monto);
        movimiento.setDescripcion("Retiro cajero automático");
        movimientosRepository.save(movimiento);

        return new RetiroResponseDTO(cuentaId, monto, saldoActual, saldoNuevo, hoy);
    }

    private EstadoCuentaAnual buscarEstado(Long cuentaId) {
        return estadoRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
    }
}
