package com.bancoxyz.bank_batch_system.service;

import com.bancoxyz.bank_batch_system.dto.web.EstadoCuentaWebDTO;
import com.bancoxyz.bank_batch_system.dto.web.InteresWebDTO;
import com.bancoxyz.bank_batch_system.dto.web.MovimientoWebDTO;
import com.bancoxyz.bank_batch_system.dto.web.TransaccionWebDTO;
import com.bancoxyz.bank_batch_system.exception.CuentaNoEncontradaException;
import com.bancoxyz.bank_batch_system.model.EstadoCuentaAnual;
import com.bancoxyz.bank_batch_system.repository.CuentaAnualRepository;
import com.bancoxyz.bank_batch_system.repository.EstadoCuentaAnualRepository;
import com.bancoxyz.bank_batch_system.repository.InteresRepository;
import com.bancoxyz.bank_batch_system.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebBffService {

    private static final double UMBRAL_ANOMALIA = 3000.0;

    private final EstadoCuentaAnualRepository estadoRepository;
    private final CuentaAnualRepository movimientosRepository;
    private final TransaccionRepository transaccionRepository;
    private final InteresRepository interesRepository;

    public WebBffService(EstadoCuentaAnualRepository estadoRepository,
            CuentaAnualRepository movimientosRepository,
            TransaccionRepository transaccionRepository,
            InteresRepository interesRepository) {
        this.estadoRepository = estadoRepository;
        this.movimientosRepository = movimientosRepository;
        this.transaccionRepository = transaccionRepository;
        this.interesRepository = interesRepository;
    }

    public EstadoCuentaWebDTO obtenerEstadoCuenta(Long cuentaId) {
        EstadoCuentaAnual estado = estadoRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));

        List<MovimientoWebDTO> movimientos = movimientosRepository.findByCuentaIdOrderByFechaDesc(cuentaId).stream()
                .map(m -> new MovimientoWebDTO(m.getFecha(), m.getTransaccion(), m.getMonto(), m.getDescripcion()))
                .toList();

        return new EstadoCuentaWebDTO(
                estado.getCuentaId(), estado.getCantidadMovimientos(), estado.getTotalDepositos(),
                estado.getTotalRetiros(), estado.getTotalOtros(), estado.getSaldoFinal(),
                estado.getFechaGeneracion(), movimientos);
    }

    public List<TransaccionWebDTO> listarTransacciones() {
        return transaccionRepository.findAll().stream()
                .map(t -> new TransaccionWebDTO(t.getId(), t.getFecha(), t.getMonto(), t.getTipo(),
                        esAnomalia(t.getMonto())))
                .toList();
    }

    public List<InteresWebDTO> historialIntereses(Long cuentaId) {
        return interesRepository.findByCuentaId(cuentaId).stream()
                .map(i -> new InteresWebDTO(i.getId(), i.getCuentaId(), i.getNombre(), i.getEdad(), i.getTipo(),
                        i.getSaldo()))
                .toList();
    }

    private boolean esAnomalia(Double monto) {
        return monto != null && (monto <= 0.0 || monto >= UMBRAL_ANOMALIA);
    }
}