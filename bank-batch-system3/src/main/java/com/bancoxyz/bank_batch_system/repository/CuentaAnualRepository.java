package com.bancoxyz.bank_batch_system.repository;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaAnualRepository extends JpaRepository<CuentaAnual, Long> {
    List<CuentaAnual> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}